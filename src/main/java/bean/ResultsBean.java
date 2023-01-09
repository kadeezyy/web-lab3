package bean;

import com.google.gson.GsonBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ValidationException;
import lombok.*;
import lombok.experimental.FieldDefaults;
import entity.Result;
import util.Checker;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@ApplicationScoped
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResultsBean implements Serializable {
    List<Result> results = new CopyOnWriteArrayList<>();
    Result current = new Result();
    Map<String, List<Result>> res = new HashMap<>();
    @Inject
    DataBaseBean dataBaseBean;
    List<Result> emptyResultsList = new CopyOnWriteArrayList<>();
    String sessionId;
    @PostConstruct
    public void postInit() {
        sessionId = getCurrentSessionId();
        res.put(sessionId, dataBaseBean.getResults());
    }

    public void addResultFromPlot() {
        var params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        sessionId = getCurrentSessionId();
        try {
            double x = Double.parseDouble(params.get("x")),
                    y = Double.parseDouble(params.get("y"));

            if (x >= -3 && x <= 5 && y >= -3 && y <= 3) {
                current.setX(x);
                current.setY(y);
                current.setSuccessful(Checker.isOnPlot(current.getX(), current.getY(), current.getR()));
                current.setTime(System.currentTimeMillis());
                current.setSessionId(sessionId);
                newResult();
            } else throw new ValidationException();
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Validation Error",
                    "Wrong parameters values."
            ));
        }
    }

    public void newResult() {
        sessionId = getCurrentSessionId();
        dataBaseBean.addResultToDataBase(current);
        addToMapIfExists(sessionId, current);
        current = current.clone();
    }

    public void clearResult() {
        sessionId = getCurrentSessionId();
        dataBaseBean.clearDataBase();
        res.replace(sessionId, emptyResultsList);
    }

    private List<Result> getIfEmptyListById(String id) {
        return res.get(id) != null ? res.get(id) : emptyResultsList;
    }

    public String parseResultsToJson() {
        sessionId = getCurrentSessionId();
        return new GsonBuilder().create().toJson(getIfEmptyListById(sessionId).stream()
                .peek(result -> result.setSuccessful(Checker.isOnPlot(
                        result.getX(),
                        result.getY(),
                        current.getR()
                )))
                .toArray());
    }

    private void addToMapIfExists(String key, Result result) {
        if (res.containsKey(key)) {
            res.get(key).add(result);
        } else {
            res.put(key, dataBaseBean.getResults());
        }
    }
    private String getCurrentSessionId() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionId(false);
    }
}
