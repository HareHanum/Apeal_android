package il.co.apeal.app.MVP;

public class BasePresenter<V extends ViewPresenterContract.View> implements ViewPresenterContract.Presenter<V> {

    public V view;

    public BasePresenter() {

    }

    public BasePresenter(V view) {
        attachView(view);
    }

    @Override
    public void attachView(V view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }
}
