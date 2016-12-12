package akressiopertti.repository;

import akressiopertti.domain.Recipe;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SearchRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public List searchRecipes(String searchText) {
        FullTextEntityManager fullTextEntityManager = 
                org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder =
                fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Recipe.class).get();
        org.apache.lucene.search.Query query = queryBuilder
                .keyword()
                .wildcard()
                .onFields("title", "source", "comment", "instructions")
                .matching(searchText + "*")
                .createQuery();
        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(query, Recipe.class);
        @SuppressWarnings("unchecked")
        List results = jpaQuery.getResultList();
        return results;
    }
    
}
