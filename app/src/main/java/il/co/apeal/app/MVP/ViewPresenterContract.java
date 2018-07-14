package il.co.apeal.app.MVP;

public interface ViewPresenterContract {
    interface View {

    }

    interface Presenter<V extends View> {
        void attachView(V view);
        void detachView();
    }
}
