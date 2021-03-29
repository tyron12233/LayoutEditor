package com.tyron.layouteditor.toolbox;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.tyron.layouteditor.values.Null;
import com.tyron.layouteditor.values.Value;

/**
 * Result class hold the return status and the data
 *
 * @author aditya.sharat
 */
public class Result {

    /**
     * Indicates that a valid {@link Value} was found at the specified data path.
     */
    public static final int RESULT_SUCCESS = 0;

    /**
     * Indicates that the object does not have the specified data path.
     */
    public static final int RESULT_NO_SUCH_DATA_PATH_EXCEPTION = -1;

    /**
     * Indicates that the data path specified is invalid. As an example, looking for a
     * property inside a {@link com.tyron.layouteditor.values.Primitive} or {@link com.tyron.layouteditor.values.Array}.
     */
    public static final int RESULT_INVALID_DATA_PATH_EXCEPTION = -2;

    /**
     * Indicates that the data path prematurely led to a {@link com.tyron.layouteditor.values.Null}
     */
    public static final int RESULT_NULL_EXCEPTION = -3;

    /**
     * singleton for No Such Data Path Exception.
     */
    public static final Result NO_SUCH_DATA_PATH_EXCEPTION = new Result(Result.RESULT_NO_SUCH_DATA_PATH_EXCEPTION, Null.INSTANCE);

    /**
     * singleton for Invalid Data Path Exception.
     */
    public static final Result INVALID_DATA_PATH_EXCEPTION = new Result(Result.RESULT_INVALID_DATA_PATH_EXCEPTION, Null.INSTANCE);

    /**
     * singleton for Null Exception.
     */
    public static final Result NULL_EXCEPTION = new Result(Result.RESULT_NULL_EXCEPTION, Null.INSTANCE);

    /**
     * Indicates the return status of the method for a given data path. The return value
     * will be {@code RESULT_SUCCESS} if and only if the data path exists and contains
     * a valid {@link Value}.
     */

    @ResultCode
    public final int RESULT_CODE;
    /**
     * The value at the specified data path.
     * {@code value} will be null if {@code RESULT_CODE} != {@code RESULT_SUCCESS}
     */

    @NonNull
    public final Value value;

    public Result(@ResultCode int RESULT_CODE, @NonNull Value value) {
        this.RESULT_CODE = RESULT_CODE;
        this.value = value;
    }

    /**
     * This method return a {@link Result} object with {@code RESULT_CODE} == {@code RESULT_SUCCESS}
     * and {@code Result#value} == {@code value}.
     *
     * @param value The {@link Value} to be wrapped.
     * @return A {@link Result} object with with {@code RESULT_CODE} == {@code RESULT_SUCCESS}.
     */
    public static Result success(Value value) {
        return new Result(RESULT_SUCCESS, value);
    }

    /**
     * @return true if and only if {@code RESULT_CODE} == {@code RESULT_SUCCESS}.
     */
    public boolean isSuccess() {
        return this.RESULT_CODE == RESULT_SUCCESS;
    }

    @IntDef({RESULT_INVALID_DATA_PATH_EXCEPTION, RESULT_NO_SUCH_DATA_PATH_EXCEPTION, RESULT_SUCCESS, RESULT_NULL_EXCEPTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ResultCode {
    }

}