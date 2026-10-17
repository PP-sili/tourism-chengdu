package org.example.tourismchengdu.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateAvatarRequest {

    @NotBlank(message = "头像URL不能为空")
    @Size(max = 255, message = "头像URL过长")
    @Pattern(regexp = "^/uploads/.+", message = "头像URL格式不正确")
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
