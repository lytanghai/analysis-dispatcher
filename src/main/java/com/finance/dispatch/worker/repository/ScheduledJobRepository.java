package com.finance.dispatch.worker.repository;

import com.finance.dispatch.worker.entity.ScheduledJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, Long>, JpaSpecificationExecutor<ScheduledJob> {

    List<ScheduledJob> findAllByEnabledTrue();

    ScheduledJob findByJobName(String jobName);

    @Modifying
    @Query("""
    DELETE FROM ScheduledJob s WHERE s.jobName = :jobName
    """)
    int deleteByJobName(@Param("jobName") String jobName);
}