package com.example.demo.transportation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransportationRepository extends JpaRepository<Transportation, Long> {

    Page<Transportation> findAllByIsVisibleTrue(Pageable pageable);

    @Query(value = "SELECT * FROM transportations t " +
            "WHERE (t.operating_days_mask & :dayBit) <> 0" +
            " and t.origin_location_id = :originId "
            , nativeQuery = true)
    List<Transportation> findByOriginId(Long originId, int dayBit);


    @Query(value = "SELECT * FROM transportations t " +
            "WHERE (t.operating_days_mask & :dayBit) <> 0" +
            " and t.destination_location_id  =:destinationId "
            , nativeQuery = true)
    List<Transportation> findByDestinationId(Long destinationId, int dayBit);


    @Query(value = "SELECT * FROM transportations t " +
            "WHERE (t.operating_days_mask & :dayBit) <> 0" +
            " and t.transportation_type = :type" +
            " and t.origin_location_id in  ( " +
            "                       Select d.destination_location_id from transportations d " +
            "                       where d.origin_location_id = :originId  " +
            "                           ) "
            , nativeQuery = true)
    List<Transportation> findTransferListByOriginIdAndTransferType(Long originId, int dayBit, String type);


}
