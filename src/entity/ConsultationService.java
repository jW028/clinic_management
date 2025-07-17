package entity;

public class ConsultationService {
    private String serviceId;
    private String serviceName;
    private double serviceCharge;

    public ConsultationService (String serviceId, String serviceName, double serviceCharge){
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceCharge = serviceCharge;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServiceCharge(double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    @Override
    public String toString() {
        return "Service ID: " + serviceId +
                "\nService Name: " + serviceName +
                "\nService Charge: " + serviceCharge;
    }
}
