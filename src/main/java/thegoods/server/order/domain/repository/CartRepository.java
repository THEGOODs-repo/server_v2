package thegoods.server.order.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.common.enums.CartStatus;
import thegoods.server.member.domain.Member;
import thegoods.server.order.domain.Cart;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findAllByMemberAndItemIdAndCartStatus(Member member, Long itemId, CartStatus cartStatus);

    List<Cart> findAllByMemberAndCartStatusOrderByItemIdAsc(Member member, CartStatus cartStatus);

    Optional<Cart> findByMemberAndItemAndItemOptionAndCartStatus(Member member, Item item, ItemOption itemOption, CartStatus cartStatus);

    Optional<Cart> findByMemberAndItemAndCartStatus(Member member, Item item, CartStatus cartStatus);
}
