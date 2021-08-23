package org.java2uml.java2umlapi.restControllers.dependencyMatrix

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.java2uml.java2umlapi.dependencyMatrix.DefaultDependencyMatrix
import org.java2uml.java2umlapi.executor.ExecutorWrapper
import org.java2uml.java2umlapi.parsedComponent.ParsedComponent
import org.java2uml.java2umlapi.parsedComponent.SourceComponent
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService
import org.java2uml.java2umlapi.restControllers.LWControllers.SourceController
import org.java2uml.java2umlapi.restControllers.ProjectInfoController
import org.java2uml.java2umlapi.restControllers.SwaggerDescription
import org.java2uml.java2umlapi.restControllers.UMLController
import org.java2uml.java2umlapi.restControllers.response.ErrorResponse
import org.java2uml.java2umlapi.restControllers.services.SSEEmitterCache
import org.java2uml.java2umlapi.restControllers.services.SSEEmitterCache.SSEventType
import org.slf4j.LoggerFactory
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Endpoint for generating dependency matrix.
 *
 * @author kawaiifoxx
 * @since 1.2.0
 */
@Tag(name = "Dependency Matrix", description = "Generate dependency matrix for a project.")
@RestController
@RequestMapping("/api/dependency-matrix")
class DependencyMatrixController(
    private val sourceComponentService: SourceComponentService,
    private val executor: ExecutorWrapper,
    private val emitterCache: SSEEmitterCache
) {
    private enum class Result { SUCCEEDED }

    private val dependencyMatrixCache: MutableMap<Long, DefaultDependencyMatrix> = ConcurrentHashMap()
    private val logger = LoggerFactory.getLogger(DependencyMatrixController::class.java)
    private val submittedTasks: MutableSet<Long> = HashSet()
    private val timeOut = 3L
    private val timeUnit = TimeUnit.SECONDS


    @Operation(
        summary = "Get Dependency Matrix",
        description = "Generate dependency matrix for an existing project by providing its id."
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = SwaggerDescription.OK_200_RESPONSE
        ), ApiResponse(
            responseCode = "202",
            description = SwaggerDescription.ACCEPTED_DESC_202,
            content = [Content(
                mediaType = SwaggerDescription.ERR_RESPONSE_MEDIA_TYPE,
                schema = Schema(implementation = ErrorResponse::class)
            )]
        ), ApiResponse(
            responseCode = "500",
            description = SwaggerDescription.INTERNAL_SERVER_ERROR_DESC,
            content = [Content(
                mediaType = SwaggerDescription.ERR_RESPONSE_MEDIA_TYPE,
                schema = Schema(implementation = ErrorResponse::class)
            )]
        )]
    )
    @GetMapping("/{projectId}")
    fun get(
        @Parameter(description = SwaggerDescription.PROJECT_ID_DESC) @PathVariable("projectId")
        projectId: Long
    ): EntityModel<DefaultDependencyMatrix> {

        if (dependencyMatrixCache.contains(projectId)) return getEntityModelOf(
            dependencyMatrixCache[projectId]!!,
            projectId
        )

        val result = executor.submit<DefaultDependencyMatrix> {
            dependencyMatrixCache[projectId] = generateDependencyMatrix(projectId)
            notifyAboutDependencyMatrixGeneration(projectId)
            dependencyMatrixCache[projectId]!!
        }

        return try {
            getEntityModelOf(result.get(timeOut, timeUnit), projectId)
        } catch (ex: ExecutionException) {
            logger.warn("One of threads in executor service threw exception with message: {}", ex.message)
            submittedTasks.remove(projectId)
            if (ex.cause is ResponseStatusException) {
                throw (ex.cause as ResponseStatusException)
            }

            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error occurred", ex)
        } catch (ex: InterruptedException) {
            submittedTasks.remove(projectId)
            logger.info("Thread interrupted in between.")

            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Task Interrupted in between.")
        } catch (ex: TimeoutException) {
            logger.info("Timed Out, proceeding with task at hand")

            throw ResponseStatusException(HttpStatus.ACCEPTED, "Please wait, your request is being processed.")
        }
    }


    private fun generateDependencyMatrix(projectId: Long): DefaultDependencyMatrix {
        val sourceComponent = getSourceComponent(projectId)

        val allParsedCompositeComponents = sourceComponent
            .children.values.map(ParsedComponent::asParsedCompositeComponent).map { it.get() }.toMutableList()

        allParsedCompositeComponents.addAll(
            sourceComponent.externalComponents.values
                .map(ParsedComponent::asParsedCompositeComponent).map { it.get() }
        )

        return DefaultDependencyMatrix(allParsedCompositeComponents, sourceComponent.relationsList)
    }


    private fun getSourceComponent(projectId: Long): SourceComponent =
        sourceComponentService[projectId].orElseThrow {
            ResponseStatusException(
                HttpStatus.ACCEPTED,
                "Files are being parsed currently please check back in a few moments"
            )
        }

    private fun getEntityModelOf(dependencyMatrix: DefaultDependencyMatrix, id: Long) =
        EntityModel.of(dependencyMatrix)
            .add(linkTo(methodOn(DependencyMatrixController::class.java).get(id)).withSelfRel())
            .add(linkTo(methodOn(ProjectInfoController::class.java).one(id)).withRel("projectInfo"))
            .add(linkTo(methodOn(SourceController::class.java).findByProjectId(id)).withRel("projectModel"))
            .add(linkTo(methodOn(UMLController::class.java).getPUMLCode(id)).withRel("umlText"))
            .add(linkTo(methodOn(UMLController::class.java).getSvg(id)).withRel("umlSvg"))


    private fun notifyAboutDependencyMatrixGeneration(id: Long) {
        if (!emitterCache.contains(id, SSEventType.DEPENDENCY_MATRIX_GENERATION)) return
        val emitter: SseEmitter = emitterCache.get(id, SSEventType.DEPENDENCY_MATRIX_GENERATION)

        try {
            emitter.send(SseEmitter.event().name("DependencyMatrix Generation").data(Result.SUCCEEDED))
        } catch (e: IOException) {
            logger.info("Unable to send event.", e)
        } finally {
            emitter.complete()
        }
    }
}