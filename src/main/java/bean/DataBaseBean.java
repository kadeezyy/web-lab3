package bean;

import entity.Result;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@SessionScoped
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataBaseBean implements Serializable {
    final EntityManagerFactory entityManagerFactory =
            Persistence.createEntityManagerFactory("connection");

    List<Result> results = new CopyOnWriteArrayList<>();
    String sessionID;
    public List<Result> getResults() {
        sessionID = FacesContext.getCurrentInstance().getExternalContext().getSessionId(false);
        initTransaction(manager -> results.addAll(manager
                .createQuery("SELECT result FROM Result result WHERE result.sessionId = ?1", Result.class)
                        .setParameter(1, sessionID)
                .getResultList()
        ));
        return results;
    }

    public void addResultToDataBase(Result result) {
        initTransaction(manager -> manager.persist(result));
    }

    public void clearDataBase() {
        sessionID = FacesContext.getCurrentInstance().getExternalContext().getSessionId(false);
        initTransaction(manager -> manager.createQuery("DELETE FROM Result WHERE sessionId=?1")
                .setParameter(1, sessionID));
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
            System.out.println("----------An exception occurred during transaction---------");
//            ex.printStackTrace();
        } finally {
            manager.close();
        }
    }
}
