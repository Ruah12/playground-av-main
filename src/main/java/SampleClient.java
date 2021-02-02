import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Patient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class SampleClient {

    public static void main(String[] theArgs) {

        // Create a FHIR client
        FhirContext fhirContext = FhirContext.forR4();
        IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        client.registerInterceptor(new LoggingInterceptor(true));

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
        for (Bundle.BundleEntryComponent entryComponent : response.getEntry())
        {
            allPatients.add((Patient) entryComponent.getResource());
            Patient patient = (Patient) entryComponent.getResource();
        }

        allPatients.sort(Comparator.comparing(a -> a.getName().get(0).getGivenAsSingleString().toString(), String.CASE_INSENSITIVE_ORDER));
        for (Patient patient : allPatients)
        {
            Date birthDate = patient.getBirthDateElement().getValue();
            String stringDate = "Not Specified.";
            if(birthDate != null)
            {
                SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
                stringDate = DateFor.format(birthDate);
            }

            System.out.println(patient.getName().get(0).getGivenAsSingleString() + " " +
                    patient.getName().get(0).getFamilyElement()+ " BirthDate: " + stringDate);
        }
    }

}
