package za.ac.cput.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank(message = "Reset code is required")
    @Size(min = 6, max = 6, message = "Reset code must be 6 digits")
    private String code;

    @NotBlank(message = "New password is required")
    private String newPassword;

    public ResetPasswordRequest() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
