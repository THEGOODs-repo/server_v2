package thegoods.server.member.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import thegoods.server.member.domain.Address;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findAllByMember_Id(Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Address a SET a.addressName = :addressName, a.addressSpec = :addressSpec, a.deliveryMemo = :deliveryMemo, a.zipcode = :zipcode, a.defaultCheck = :defaultCheck, a.recipientName = :recipientName, a.recipientPhone = :recipientPhone WHERE a.id = :addressId")
    void changeAddress(Long addressId, String addressName, String addressSpec,String deliveryMemo,String zipcode, Boolean defaultCheck, String recipientPhone, String recipientName);
}
