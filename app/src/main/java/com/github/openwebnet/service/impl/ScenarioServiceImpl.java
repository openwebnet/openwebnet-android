package com.github.openwebnet.service.impl;

import com.github.niqdev.openwebnet.OpenSession;
import com.github.niqdev.openwebnet.message.Scenario;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.ScenarioModel;
import com.github.openwebnet.repository.ScenarioRepository;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.ScenarioService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static com.github.openwebnet.model.ScenarioModel.Status;
import static com.github.openwebnet.model.ScenarioModel.Status.START;
import static com.github.openwebnet.model.ScenarioModel.Status.STOP;

public class ScenarioServiceImpl implements ScenarioService {

    private static final Logger log = LoggerFactory.getLogger(ScenarioService.class);

    @Inject
    ScenarioRepository scenarioRepository;

    @Inject
    CommonService commonService;

    public ScenarioServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public Observable<String> add(ScenarioModel scenario) {
        return scenarioRepository.add(scenario);
    }

    @Override
    public Observable<Void> update(ScenarioModel scenario) {
        return scenarioRepository.update(scenario);
    }

    @Override
    public Observable<Void> delete(String uuid) {
        return scenarioRepository.delete(uuid);
    }

    @Override
    public Observable<ScenarioModel> findById(String uuid) {
        return scenarioRepository.findById(uuid);
    }

    @Override
    public Observable<List<ScenarioModel>> findByEnvironment(Integer id) {
        return scenarioRepository.findByEnvironment(id);
    }

    @Override
    public Observable<List<ScenarioModel>> findFavourites() {
        return scenarioRepository.findFavourites();
    }

    private Func1<String, Scenario> requestStatus = Scenario::requestStatus;

    private Func2<OpenSession, ScenarioModel, ScenarioModel> handleStatus = (openSession, scenario) -> {
        Scenario.handleStatus(
            () -> scenario.setStatus(START),
            () -> scenario.setStatus(STOP),
            () -> scenario.setEnable(true),
            () -> scenario.setEnable(false)
        ).call(openSession);
        return scenario;
    };

    @Override
    public Observable<List<ScenarioModel>> requestByEnvironment(Integer id) {
        return findByEnvironment(id)
            .flatMapIterable(lightModels -> lightModels)
            .flatMap(requestScenario(requestStatus, handleStatus))
            .collect(ArrayList::new, List::add);
    }

    @Override
    public Observable<List<ScenarioModel>> requestFavourites() {
        return findFavourites()
            .flatMapIterable(scenarioModels -> scenarioModels)
            .flatMap(requestScenario(requestStatus, handleStatus))
            .collect(ArrayList::new, List::add);
    }

    private Func2<OpenSession, ScenarioModel, ScenarioModel> handleResponse(Status status) {
        return (openSession, scenario) -> {
            Scenario.handleResponse(() -> scenario.setStatus(status), () -> scenario.setStatus(null))
                .call(openSession);
            return scenario;
        };
    }

    @Override
    public Observable<ScenarioModel> start(ScenarioModel scenario) {
        return Observable.just(scenario).flatMap(requestScenario(Scenario::requestStart, handleResponse(START)));
    }

    @Override
    public Observable<ScenarioModel> stop(ScenarioModel scenario) {
        return Observable.just(scenario).flatMap(requestScenario(Scenario::requestStop, handleResponse(STOP)));
    }

    private Func1<ScenarioModel, Observable<ScenarioModel>> requestScenario(
        Func1<String, Scenario> request, Func2<OpenSession, ScenarioModel, ScenarioModel> handler) {
        // TODO improvement: group by gateway and for each gateway send all requests together
        return scenario -> commonService.findClient(scenario.getGatewayUuid())
            .send(request.call(scenario.getWhere()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(openSession -> handler.call(openSession, scenario))
            .onErrorReturn(throwable -> {
                log.warn("scenario={} | failing request={}", scenario.getUuid(), request.call(scenario.getWhere()).getValue());
                // unreadable status
                return scenario;
            });
    }

}
