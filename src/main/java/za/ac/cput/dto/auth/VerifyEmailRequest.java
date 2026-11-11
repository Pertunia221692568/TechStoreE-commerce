package za.ac.cput.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VerifyEmailRequest {

    @NotBlank(message = "Verification code is required")
    @Size(min = 6, max = 6, message = "Verification code must be 6 digits")
    private String code;

    public VerifyEmailRequest() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
