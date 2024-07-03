package thegoods.server.order.implement.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thegoods.server.common.enums.CartStatus;
import thegoods.server.member.domain.Member;
import thegoods.server.order.domain.Cart;
import thegoods.server.order.domain.repository.CartRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartQueryServiceImpl implements CartQueryService {
    private final CartRepository cartRepository;

    @Override
    public boolean isExistCart(Long cartId) {
        return cartRepository.existsById(cartId);
    }

    @Override
    public List<Cart> getCartsByItem(Long itemId, Member member) {
        return cartRepository.findAllByMemberAndItemIdAndCartStatus(member, itemId, CartStatus.ACTIVE);
    }

    @Override
    public List<Cart> getCartsByMember(Member member) {
        return cartRepository.findAllByMemberAndCartStatusOrderByItemIdAsc(member, CartStatus.ACTIVE);
    }
}
