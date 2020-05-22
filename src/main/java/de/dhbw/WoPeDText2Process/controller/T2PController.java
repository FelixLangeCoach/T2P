/**
 * <h1>T2PController</h1>
 *
 * <p>The T2PController is a SpringBoot specific ReST-Controller to build a WebService. This interface is used by the WoPeD Tool to create a PetriNet
 * based on the response bodies.</p>
 *
 * <p>Please do not misuse this controller for any other reason instead of building new interfaces nodes.</p>
 *
 * @author <a href="mailto:kanzler.benjamin@student.dhbe-karlsruhe.de">Benjamin Kanzler</a>
 */
package de.dhbw.WoPeDText2Process.controller;

import de.dhbw.WoPeDText2Process.exceptions.PetrinetGenerationException;
import de.dhbw.WoPeDText2Process.exceptions.InvalidInputException;
import de.dhbw.WoPeDText2Process.helper.Response;
import de.dhbw.WoPeDText2Process.helper.T2PControllerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController()
public class T2PController {

    // Initialize log4j to log information into the console
    Logger logger = LoggerFactory.getLogger(T2PController.class);

    /* Helper class to keep the controller clean.
     * Please place any methods performing the work here.
     */
    T2PControllerHelper t2PControllerHelper = new T2PControllerHelper();

    /**
     * <h1>generatePetriNetFromText</h1>
     *
     * <p>POST based controller method. Tailored to generate a PetriNet from a given Text.
     * Use the proper URL to get generate the String which can be interpreted by the WoPeD-Tool.</p>
     *
     * @author <a href="mailto:kanzler.benjamin@student.dhbe-karlsruhe.de">Benjamin Kanzler</a>
     * @param param:
     * @return A generic Response Object with the PNML-String in the response attribute.
     */
    @PostMapping(value = "/generatePNML", consumes = "application/json", produces = "application/json")
    public String generatePetriNetFromText(@RequestBody String param, HttpServletRequest request) {

        Response<String> pnmlResponse;
        logger.info("Trying to generate a PetriNet with the given String parameter");
        try {
            logger.info("Validating the String information, scanning for incompatible characters");
            t2PControllerHelper.checkInputValidity(param);
            logger.info("Starting generating PNML-String ...");
            pnmlResponse = new Response<String>(t2PControllerHelper.generatePetrinetFromText(param));
            logger.info("Finished generating PNML-String");
        } catch (InvalidInputException e) {
            logger.error("The given parameter ist not a valid one. Please check the String and pass a correct one. More details on the error is stored in the response json");
            logger.error(e.getMessage());
            pnmlResponse = new Response<String>(true, e.getCause(), e.getMessage(), e.getStackTrace());
        } catch (PetrinetGenerationException e) {
            logger.error("The PetriNet was not properly built. Please contact the development department. <a href=mailto:woped-service@dhbw-karlsruhe.de>WoPeD-Service<a>");
            logger.error(e.getMessage());
            e.printStackTrace();
            pnmlResponse = new Response<String>(true, e.getCause(), e.getMessage(), e.getStackTrace());
        } catch (Exception e){
            logger.error("An unexpected error interrupted the process. Exception information is given to the return json");
            logger.error(e.getMessage());
            pnmlResponse = new Response<String>(true, e.getCause(), e.getMessage(), e.getStackTrace());
        } finally {
            logger.info("Resetting the NLP-Tools ...");
            //t2PControllerHelper.resetNLPTools();
        }

        logger.info("Returning the Response-Object");
        return pnmlResponse.getResponse();
    }

}