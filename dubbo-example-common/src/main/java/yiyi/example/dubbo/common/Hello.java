package yiyi.example.dubbo.common;

import java.io.Serializable;

/**
 * A Hello object.
 *
 * @author shuaicj 2018/03/08
 */
@SuppressWarnings("serial")
public class Hello implements Serializable {

    private long id;
    private String message;

    public Hello(long id, String message) {
        this.id = id;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Hello{"
                + "id=" + id
                + ", message='" + message + '\''
                + '}';
    }
}
