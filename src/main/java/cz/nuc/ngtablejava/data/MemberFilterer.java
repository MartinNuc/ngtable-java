package cz.nuc.ngtablejava.data;

import cz.nuc.ngtablejava.model.Car;
import cz.nuc.ngtablejava.model.Member;
import cz.nuc.ngtablejava.model.Member_;
import cz.nuc.ngtablejava.rest.FilterParam;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class MemberFilterer {
    private static final int DEFAULT_MAX_RESULT_PER_PAGE = 10;
    private final EntityManager entityManager;
    private CriteriaBuilder cb;
    private CriteriaQuery query;
    private Predicate predicate;
    private Integer firstResult;
    private Integer itemsPerPage;

    /**
     * Extend these to add other entities
     */
    private Root<Member> memberRoot;
    private Join<Member, Car> carJoin;

    public MemberFilterer(EntityManager entityManager, FilterParam param) {
        this.entityManager = entityManager;
        initialize();
        initializeEntities();
        createQueryConstraints(param);
    }

    /**
     * ***************************************************
     * Extend these to add other entities
     */
    private Path findTargetEntityFromName(String entityName) {
        Path targetEntity;
        // find target entity
        if (entityName.equals("cars")) {
            targetEntity = carJoin;
        } else {
            throw new RuntimeException("Invalid entity (column) name: " + entityName);
        }
        return targetEntity;
    }

    private void initializeEntities() {
        memberRoot = query.from(Member.class);
        carJoin = memberRoot.join(Member_.cars, JoinType.LEFT);
    }

    /**
     * **************************************************
     */

    private void createQueryConstraints(FilterParam param) {

        firstResult = param.firstResult;
        itemsPerPage = param.itemsPerPage;

        if (param.filterByFields != null && param.filterByFields.size() > 0) {
            setupFilterByFields(param.filterByFields);
        }

        if (param.orderBy != null && !param.orderBy.isEmpty()) {
            setupOrderBy(param.orderBy);
        }
    }

    private void setupOrderBy(Map<String, String> orderBy) {
        Iterator<Map.Entry<String, String>> it = orderBy.entrySet().iterator();
        while (it.hasNext()) {
            addOrderByField((Map.Entry) it.next());
            it.remove();
        }
    }

    private void addOrderByField(Map.Entry<String, String> pairs) {
        Path path;
        try {
            // try to get field directly
            path = memberRoot.get(pairs.getKey());
        } catch (Exception e) {
            // if field doesnt exist try to find it by string path
            path = getFieldFromStringPath(pairs.getKey());
        }
        if (pairs.getValue().equals("desc")) {
            query.orderBy(cb.desc(path));
        } else {
            query.orderBy(cb.asc(path));
        }
    }

    private Path getFieldFromStringPath(String stringPath) {
        Path targetEntity;
        String[] segments = stringPath.split("\\."); // ["cars", "name"]
        targetEntity = findTargetEntityFromName(segments[0]);
        return targetEntity.get(segments[1]);  // return field
    }

    private void setupFilterByFields(Map<String, String> filterByFields) {
        initializeTruePredicate();

        Iterator<Map.Entry<String, String>> it = filterByFields.entrySet().iterator();
        while (it.hasNext()) {
            addPredicateForFilteringByField(it.next());
            it.remove();
        }
    }

    private void initializeTruePredicate() {
        predicate = cb.equal(cb.literal(1), 1);
    }

    private void addPredicateForFilteringByField(Map.Entry<String, String> pair) {
        try {
            addPredicate(memberRoot, pair.getKey(), pair.getValue());
        } catch (NoSuchFieldException e) {
            String stringPath = pair.getKey();  // eg. "cars.name"
            String filterValue = pair.getValue();

            String[] segments = stringPath.split("\\."); // results in: ["cars", "name"]
            Path field = findTargetEntityFromName(segments[0]);
            String columnName = segments[1];

            try {
                addPredicate(field, columnName, filterValue);
            } catch (NoSuchFieldException e1) {
                throw new RuntimeException("Invalid entity (column) name: " + columnName);
            }
        }
    }

    private void addPredicate(Path targetEntity, String fieldName, String value) throws NoSuchFieldException {
        Field entityDeclaredField = Member.class.getDeclaredField(fieldName);
        Class type = entityDeclaredField.getType();

        if (type.getName().equals("java.lang.Long") || type.getName().equals("java.lang.Integer")) {
            // ToDo: this is a potential bug where we treat Integer as Long
            Path field = targetEntity.<Long>get(fieldName);
            addExactNumericPredicate(field, value);
        } else {
            Path field = targetEntity.<String>get(fieldName);
            addStringLikePredicate(field, value);
        }
    }

    private void addExactNumericPredicate(Path field, String value) {
        predicate = cb.and(predicate, cb.equal(field, Long.parseLong(value)));
    }

    private void addStringLikePredicate(Path field, String value) {
        predicate = cb.and(predicate, cb.like(field, "%" + value + "%"));
    }

    private void initialize() {
        cb = entityManager.getCriteriaBuilder();
        query = cb.createQuery();
        predicate = cb.equal(cb.literal(1), 1);
    }

    private CriteriaQuery<Member> getFilteringQuery() {
        query.select(memberRoot).distinct(true);
        query.where(predicate);
        return query;
    }

    private CriteriaQuery<Long> getQueryForCount() {
        query.select(cb.countDistinct(memberRoot.get(Member_.id)));
        query.where(predicate);
        return query;
    }

    private void setLimits(TypedQuery<Member> qr) {
        setupFirstResultLimit(qr);
        setupItemsPerPage(qr);
    }

    private void setupItemsPerPage(TypedQuery<Member> qr) {
        if (itemsPerPage != null) {
            qr.setMaxResults(itemsPerPage);
        } else {
            qr.setMaxResults(DEFAULT_MAX_RESULT_PER_PAGE);
        }
    }

    private void setupFirstResultLimit(TypedQuery<Member> qr) {
        if (firstResult != null) {
            qr.setFirstResult(firstResult);
        } else {
            qr.setFirstResult(0);
        }
    }

    public List<Member> list() {
        TypedQuery<Member> q = this.entityManager.createQuery(this.getFilteringQuery());
        setLimits(q);
        return q.getResultList();
    }

    public Long count() {
        TypedQuery<Long> q = this.entityManager.createQuery(this.getQueryForCount());
        return q.getSingleResult();
    }
}