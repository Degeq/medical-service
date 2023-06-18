import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;

public class MedicalServiceTest {

    private MedicalServiceImpl medicalService;
    private PatientInfoFileRepository repository;
    private SendAlertServiceImpl alertService;
    private PatientInfo patientInfo;
    private HealthInfo healthInfo;

    @Test
    public void testCheckBloodPressureMessage() {
        //arrange
        healthInfo = Mockito.mock(HealthInfo.class);
        Mockito.when(healthInfo.getBloodPressure())
                .thenReturn(new BloodPressure(120,90));

        String id = "4er";

        patientInfo = Mockito.mock(PatientInfo.class);
        Mockito.when(patientInfo.getHealthInfo())
                .thenReturn(healthInfo);
        Mockito.when(patientInfo.getId())
                .thenReturn(id);


        repository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(repository.getById(Mockito.anyString()))
                .thenReturn(patientInfo);


        alertService = Mockito.mock(SendAlertServiceImpl.class);

        medicalService = new MedicalServiceImpl(repository, alertService);

        String expected = String.format("Warning, patient with id: %s, need help", id);

        //act
        medicalService.checkBloodPressure("4er", new BloodPressure(140, 90));

        //assert
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(alertService).send(argumentCaptor.capture());
        Assertions.assertEquals(expected, argumentCaptor.getValue());

    }

    /*
    В методе checkTemperature некорректная логика в if - subtract().compareTo() > 0 - предполагает уменьшение значение
    нормальной температуры на 1.5 градуса, при этом compareTo() возвращает 1 только в случае, если значение, для которого
    вызывается метод больше того, с которым оно сравнивается, а значит, сервис отправляет уведомления только в случае,
    когда фактическая температура ниже нормальной.
     */
    @Test
    public void testCheckTemperatureMessage() {
        //arrange
        healthInfo = Mockito.mock(HealthInfo.class);
        Mockito.when(healthInfo.getNormalTemperature())
                .thenReturn(new BigDecimal("36.6"));

        String id = "4er";

        patientInfo = Mockito.mock(PatientInfo.class);
        Mockito.when(patientInfo.getHealthInfo())
                .thenReturn(healthInfo);
        Mockito.when(patientInfo.getId())
                .thenReturn(id);


        repository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(repository.getById(Mockito.anyString()))
                .thenReturn(patientInfo);


        alertService = Mockito.mock(SendAlertServiceImpl.class);

        medicalService = new MedicalServiceImpl(repository, alertService);

        String expected = String.format("Warning, patient with id: %s, need help", id);

        //act
        medicalService.checkTemperature("4er", new BigDecimal("35"));

        //assert
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(alertService).send(argumentCaptor.capture());
        Assertions.assertEquals(expected, argumentCaptor.getValue());
    }

    @Test
    public void testCheckNormalHealthLevel() {
        //arrange
        healthInfo = Mockito.mock(HealthInfo.class);
        Mockito.when(healthInfo.getBloodPressure())
                .thenReturn(new BloodPressure(120, 90));
        Mockito.when(healthInfo.getNormalTemperature())
                .thenReturn(new BigDecimal("36.6"));

        String id = "4er";

        patientInfo = Mockito.mock(PatientInfo.class);
        Mockito.when(patientInfo.getHealthInfo())
                .thenReturn(healthInfo);


        repository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(repository.getById(Mockito.anyString()))
                .thenReturn(patientInfo);


        alertService = Mockito.mock(SendAlertServiceImpl.class);

        medicalService = new MedicalServiceImpl(repository, alertService);

        //act
        medicalService.checkTemperature("4er", new BigDecimal("38"));
        medicalService.checkBloodPressure("4er", new BloodPressure(120,90));

        //assert
        Mockito.verify(alertService, Mockito.times(0)).send(Mockito.anyString());
    }



    }
