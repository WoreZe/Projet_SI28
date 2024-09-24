package fr.utc.multeract.server.dao.request;

public record SignUpRequest(String email, String password, String firstName, String lastName, String username) {
}
