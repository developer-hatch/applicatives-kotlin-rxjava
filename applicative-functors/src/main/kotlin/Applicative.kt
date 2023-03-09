import io.reactivex.Single
import io.reactivex.functions.BiFunction

/**
 * Returns a Single that is the result of applying the function inside the context (a Single in this case).
 * This function is curried and will be used as an Applicative Functor, so each argument will be given
 * one by one
 * @param <B> the result value type
 * @param applicativeValue
 *            a Single that contains the input value of the function
 * @return the Single returned when the function is applied to the applicative value.
 * Each application will be executed on <b>a new thread</b> if and only if the Single is subscribed on a specific scheduler
 */
infix fun <A, B : Any> Single<(A) -> (B)>.zipOver(applicativeValue: Single<A>): Single<B> =
    Single.zip(this, applicativeValue) { f, a -> f(a) }

/**
 * Returns a Single that is the result of applying the function inside the context (a Single in this case).
 * This function is curried and will be used as an Applicative Functor, so each argument will be given
 * one by one
 * @param <B> the result value type
 * @param applicativeValue
 *            a Single that contains the input value of the function and it can be null
 * @return the Single returned when the function is applied to the applicative value even when
 * it is null.
 * Each application will be executed on <b>a new thread</b> if and only if the Single is subscribed on a specific scheduler
 */
infix fun <A, B : Any> Single<(A?) -> (B)>.zipOverNullable(applicativeValue: Single<A>?): Single<B> =
    when {
        applicativeValue != null -> Single.zip(this, applicativeValue, { f, a -> f(a) })
        else -> this.map { it(null) }
    }

/**
 * Each application will be executed on <b>the same thread</b> if and only if the Single is not subscribed on a specific scheduler
 */
infix fun <A, B> Single<(A) -> (B)>.mapOver(applicativeValue: Single<A>): Single<B> =
    applicativeValue.flatMap { value -> this.map { applicativeFunction -> applicativeFunction(value) } }

/**
 * Each application will be executed on <b>the same thread</b> if and only if the Single is not subscribed on a specific scheduler
 */
infix fun <A, B> Single<(A?) -> (B)>.mapOverNullable(applicativeValue: Single<A>?): Single<B> =
    when {
        applicativeValue != null -> applicativeValue.flatMap { value -> this.map { applicativeFunction -> applicativeFunction(value) } }
        else -> this.map { it(null) }
    }
