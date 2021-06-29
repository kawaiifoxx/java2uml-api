package org.java2uml.java2umlapi.restControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller enables client's to subscribe to events. A event is fired when file is parsed, A project model is
 * generated, uml is generated or a class diagram is generated.
 *
 * @author kawaiifoxx
 */
@RestController
@RequestMapping("/api")
public class EventSubscriptionController {

}
