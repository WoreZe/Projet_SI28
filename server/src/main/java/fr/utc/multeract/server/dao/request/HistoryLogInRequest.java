package fr.utc.multeract.server.dao.request;

public record HistoryLogInRequest(String code, String username, String avatar, Boolean screen) {
    public HistoryLogInRequest {
        if (screen == null) {
            screen = false;
        }
    }
}
