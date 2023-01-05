package bean;

import entity.Result;
import jakarta.enterprise.context.SessionScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import util.Checker;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@SessionScoped
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataBaseBean implements Serializable {
    static final EntityManagerFactory entityManagerFactory =
            Persistence.createEntityManagerFactory("database");

    final List<Result> results = new CopyOnWriteArrayList<>();

    public DataBaseBean() {
        initTransaction(manager -> results.addAll(manager
                .createQuery("SELECT result FROM Result result", Result.class)
                .getResultList()
        ));
    }

    public boolean addResultToDataBase(Result result) {
        try {
            initTransaction(manager -> manager.persist(result));
        } catch (Exception ex) {
            return false;
        }
        return true;
//        current.setSuccessful(Checker.isOnPlot(current.getX(), current.getY(), current.getR()));
//        current.setTime(System.currentTimeMillis());
//        results.add(current);
//        initTransaction(manager -> manager.persist(current));
//        current = current.clone();
    }

    private void initTransaction(Consumer<EntityManager> transaction) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        try {
            manager.getTransaction().begin();
            transaction.accept(manager);
            manager.getTransaction().commit();
        } catch (Exception ex) {
            if (manager.getTransaction().isActive()) {
                manager.getTransaction().rollback();
            }
            System.out.println("An exception occurred during transaction.");
            ex.printStackTrace();
        } finally {
            manager.close();
        }
    }
}
