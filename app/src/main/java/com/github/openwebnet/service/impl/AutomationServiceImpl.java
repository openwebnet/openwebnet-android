package com.github.openwebnet.service.impl;

import com.github.niqdev.openwebnet.OpenSession;
import com.github.niqdev.openwebnet.message.Automation;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.AutomationModel;
import com.github.openwebnet.model.AutomationModel.Status;
import com.github.openwebnet.repository.AutomationRepository;
import com.github.openwebnet.service.AutomationService;
import com.github.openwebnet.service.CommonService;

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

import static com.github.openwebnet.model.AutomationModel.Status.DOWN;
import static com.github.openwebnet.model.AutomationModel.Status.STOP;
import static com.github.openwebnet.model.AutomationModel.Status.UP;

public class AutomationServiceImpl implements AutomationService {

    private static final Logger log = LoggerFactory.getLogger(AutomationService.class);

    @Inject
    AutomationRepository automationRepository;

    @Inject
    CommonService commonService;

    public AutomationServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public Observable<String> add(AutomationModel automation) {
        return automationRepository.add(automation);
    }

    @Override
    public Observable<Void> update(AutomationModel automation) {
        return automationRepository.update(automation);
    }

    @Override
    public Observable<Void> delete(String uuid) {
        return automationRepository.delete(uuid);
    }

    @Override
    public Observable<AutomationModel> findById(String uuid) {
        return automationRepository.findById(uuid);
    }

    @Override
    public Observable<List<AutomationModel>> findByEnvironment(Integer id) {
        return automationRepository.findByEnvironment(id);
    }

    @Override
    public Observable<List<AutomationModel>> findFavourites() {
        return automationRepository.findFavourites();
    }

    private Func1<String, Automation> requestStatus = Automation::requestStatus;

    private Func2<OpenSession, AutomationModel, AutomationModel> handleStatus = (openSession, automation) -> {
        Automation.handleStatus(() -> automation.setStatus(STOP), () -> automation.setStatus(UP), () -> automation.setStatus(DOWN)).call(openSession);
        return automation;
    };

    @Override
    public Observable<List<AutomationModel>> requestByEnvironment(Integer id) {
        return findByEnvironment(id)
            .flatMapIterable(automationModels -> automationModels)
            .flatMap(requestAutomation(requestStatus, handleStatus))
            .collect(ArrayList::new, List::add);
    }

    @Override
    public Observable<List<AutomationModel>> requestFavourites() {
        return findFavourites()
            .flatMapIterable(automationModels -> automationModels)
            .flatMap(requestAutomation(requestStatus, handleStatus))
            .collect(ArrayList::new, List::add);
    }

    private Func2<OpenSession, AutomationModel, AutomationModel> handleResponse(Status status) {
        return (openSession, automation) -> {
            Automation.handleResponse(() ->
                automation.setStatus(status), () -> automation.setStatus(null)).call(openSession);
            return automation;
        };
    }

    @Override
    public Observable<AutomationModel> stop(AutomationModel automation) {
        Func1<String, Automation> requestStop = Automation::requestStop;

        return Observable.just(automation).flatMap(requestAutomation(requestStop, handleResponse(STOP)));
    }

    @Override
    public Observable<AutomationModel> moveUp(AutomationModel automation) {
        Func1<String, Automation> requestMoveUp = Automation::requestMoveUp;

        return Observable.just(automation).flatMap(requestAutomation(requestMoveUp, handleResponse(UP)));
    }

    @Override
    public Observable<AutomationModel> moveDown(AutomationModel automation) {
        Func1<String, Automation> requestMoveDown = Automation::requestMoveDown;

        return Observable.just(automation).flatMap(requestAutomation(requestMoveDown, handleResponse(DOWN)));
    }

    private Func1<AutomationModel, Observable<AutomationModel>> requestAutomation(
        Func1<String, Automation> request, Func2<OpenSession, AutomationModel, AutomationModel> handler) {

        return automation -> commonService.findClient(automation.getGatewayUuid())
            .send(request.call(automation.getWhere()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(openSession -> handler.call(openSession, automation))
            .onErrorReturn(throwable -> {
                log.warn("automation={} | failing request={}", automation.getUuid(), request.call(automation.getWhere()).getValue());
                // unreadable status
                return automation;
            });
    }

}
