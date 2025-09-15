package co.com.crediya.model.solicitante;

public record SolicitanteInfo(
    String documento,
    String nombre,
    Double salarioBase
) {
    public static SolicitanteInfo fromInfra(String documento, String name, Double baseSalary) {
        return new SolicitanteInfo(documento, name, baseSalary);
    }
}
