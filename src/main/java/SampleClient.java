import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SampleClient {

    public static void main(String[] theArgs) {

        // Create a FHIR client
        FhirContext fhirContext = FhirContext.forR4();
        IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        client.registerInterceptor(new LoggingInterceptor(false));

        // Search for Patient resources
        Bundle response = client
                .search()
                .forResource("Patient")
                .where(Patient.FAMILY.matches().value("SMITH"))
        //  As I understant: I have to change it to:
        //      .where(Patient.FAMILY.matches().values("Smith", "Smyth".... 20 names from resource file.))
                .returnBundle(Bundle.class)
                .execute();

        System.out.println(response.getEntry().size());

        List<Patient> allPatients = new ArrayList<>();
        for (Bundle.BundleEntryComponent entryComponent : response.getEntry()) {
            allPatients.add((Patient) entryComponent.getResource());

            Patient patient = (Patient) entryComponent.getResource();
            System.out.println(patient.getName().get(0).getGiven());
        }

        System.out.println("Once again: ");
        allPatients.sort(Comparator.comparing(a -> a.getName().get(0).getGiven().toString()));
        for (Patient patient : allPatients) {
            System.out.println(patient.getName().get(0).getGiven());
        }
    }

}
