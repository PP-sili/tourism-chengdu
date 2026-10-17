package org.example.tourismchengdu.dto.mall;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateMallOrderRequest {

    @NotBlank(message = "收货人不能为空")
    @Size(max = 50, message = "收货人姓名过长")
    private String receiverName;

    @NotBlank(message = "收货电话不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "收货电话格式不正确")
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    @Size(max = 255, message = "收货地址过长")
    private String receiverAddress;

    @Size(max = 255, message = "备注过长")
    private String remark;

    private Boolean fromCart;

    @Valid
    private List<MallOrderItemRequest> items;

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getFromCart() {
        return fromCart;
    }

    public void setFromCart(Boolean fromCart) {
        this.fromCart = fromCart;
    }

    public List<MallOrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<MallOrderItemRequest> items) {
        this.items = items;
    }
}
