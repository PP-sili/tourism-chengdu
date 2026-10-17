package org.example.tourismchengdu.dto.user;

public class UserOverviewVO {

    private UserProfileVO profile;
    private Long totalLikes;
    private Long postCount;
    private Long cartItemCount;
    private OrderStatusCountVO ticketOrders;
    private OrderStatusCountVO mallOrders;

    public UserProfileVO getProfile() {
        return profile;
    }

    public void setProfile(UserProfileVO profile) {
        this.profile = profile;
    }

    public Long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Long getPostCount() {
        return postCount;
    }

    public void setPostCount(Long postCount) {
        this.postCount = postCount;
    }

    public Long getCartItemCount() {
        return cartItemCount;
    }

    public void setCartItemCount(Long cartItemCount) {
        this.cartItemCount = cartItemCount;
    }

    public OrderStatusCountVO getTicketOrders() {
        return ticketOrders;
    }

    public void setTicketOrders(OrderStatusCountVO ticketOrders) {
        this.ticketOrders = ticketOrders;
    }

    public OrderStatusCountVO getMallOrders() {
        return mallOrders;
    }

    public void setMallOrders(OrderStatusCountVO mallOrders) {
        this.mallOrders = mallOrders;
    }
}
