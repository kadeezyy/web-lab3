package bean;

import com.google.gson.GsonBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import entity.Result;
import util.Checker;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@SessionScoped
@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResultsBean implements Serializable {

    final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("connection");
    final List<Result> results = new CopyOnWriteArrayList<>();
    Result current = new Result();
    @Inject
    DataBaseBean dataBaseBean;

    @PostConstruct
    public void postInit() {
        results.addAll(dataBaseBean.getResults());
    }

    public ResultsBean() {
    }


    public void addResultFromPlot() {
        var params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();

        try {
            double x = Double.parseDouble(params.get("x")),
                    y = Double.parseDouble(params.get("y"));

            if (x >= -3 && x <= 5 && y >= -3 && y <= 3) {
                current.setX(x);
                current.setY(y);
                current.setSuccessful(Checker.isOnPlot(current.getX(), current.getY(), current.getR()));
                newResult(current);
            } else throw new ValidationException();
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Validation Error",
                    "Wrong parameters values."
            ));
        }
    }

    public void newResult(Result result) {
        results.add(result);
        dataBaseBean.addResultToDataBase(result);
    }


    public String parseResultsToJson() {
        return new GsonBuilder().create().toJson(results.stream()
                .peek(result -> result.setSuccessful(Checker.isOnPlot(
                        result.getX(),
                        result.getY(),
                        current.getR()
                )))
                .toArray());
    }
}
