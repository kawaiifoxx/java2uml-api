package org.java2uml.java2umlapi.fileStorage;

import org.java2uml.java2umlapi.fileStorage.service.ClassDiagramSVGService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("When using class diagram service, ")
class ClassDiagramSVGServiceTest {

    @Autowired
    ClassDiagramSVGService classDiagramSVGService;

    String svgString = "Test svg string.";
    Long key = 10L;
    Long key2 = 20L;

    @BeforeEach
    void setUp() {
        assertThat(classDiagramSVGService).isNotNull()
                .describedAs("class diagram service should not be null.");
        classDiagramSVGService.save(key, svgString);
    }

    @Test
    @DisplayName("contains should return true if key is present.")
    void contains() {
        assertThat(classDiagramSVGService.contains(key)).isTrue()
                .describedAs("classDiagramSvgService should contain previously added entries.");
    }

    @Test
    @DisplayName("get should return svg string, given that  mapping is present.")
    void get() {
        assertThat(classDiagramSVGService.get(key)).isEqualTo(svgString)
                .describedAs("classDiagramSvgService should return previously added entries.");
    }

    @Test
    @DisplayName("Save should save the string.")
    void save() {
        classDiagramSVGService.save(key2, svgString);
        assertThat(classDiagramSVGService.get(key2)).isEqualTo(svgString)
                .describedAs("classDiagramSvgService should return previously added entries.");
    }

    @Test
    @DisplayName("Delete should delete the mapping")
    void delete() {
        classDiagramSVGService.delete(key);
        assertThat(classDiagramSVGService.get(key))
                .describedAs("classDiagramSvgService should return null after deleting given mapping.")
                .isNull();
    }
}