package com.leopaul29.bento.repositories;

import com.leopaul29.bento.entities.Bento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BentoRepository extends JpaRepository<Bento, Long>, JpaSpecificationExecutor<Bento>, PagingAndSortingRepository<Bento, Long> {

    /**
     * Both collections are LAZY @ManyToMany, and every caller of findAll() reads them —
     * the recommendation strategies score on tags and ingredients, and the list endpoint
     * maps them into DTOs. Outside a web request there is no open session, so the scoring
     * path threw LazyInitializationException; inside one it only worked because
     * spring.jpa.open-in-view defaults to true, at the cost of an N+1 per bento.
     * Fetching both here fixes the defect and the N+1 at once. Sets, not bags, so the
     * two join fetches deduplicate instead of raising MultipleBagFetchException.
     */
    @Override
    @EntityGraph(attributePaths = {"tags", "ingredients"})
    List<Bento> findAll();
}
