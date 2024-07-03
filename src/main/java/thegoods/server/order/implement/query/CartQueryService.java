package thegoods.server.order.implement.query;

import thegoods.server.member.domain.Member;
import thegoods.server.order.domain.Cart;

import java.util.List;

public interface CartQueryService {

    boolean isExistCart(Long cartId);

    List<Cart> getCartsByItem(Long itemId, Member member);

    List<Cart> getCartsByMember(Member member);
}
