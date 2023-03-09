import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import org.funktionale.currying.curried
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ApplicativeTest {

    @Test
    fun testZipOver() {
        //Given
        val bothSubscribed = CountDownLatch(2) // Change this value to 2 to run the test slowly
        val subscribeThreadsStillRunning = CountDownLatch(1) // Change this value to 1 to run the test slowly

        val service: (String, Int, String?, Int, String, String, String, String, String, String?, String) -> Single<String> = {
                s1: String,
                s2: Int,
                s3: String?,
                s4: Int,
                s5: String,
                s6: String,
                s7: String,
                s8: String,
                s9: String,
                s10: String?,
                s11: String ->
            val result =
                listOf(s1, "$s2", s3 ?: "none", "$s4", s5, s6, s7, s8, s9, s10 ?: "none", s11).joinToString(separator = ";")
            Single.just("Values:$result")
        }

        val createSingle = { value: String ->
            Observable
                .create<String> { emitter ->
                    println("Parallel subscribe $value on ${Thread.currentThread().name}")
                    bothSubscribed.countDown()
                    subscribeThreadsStillRunning.await(20, TimeUnit.SECONDS)
                    emitter.onNext(value)
                    emitter.onComplete()
                }
                .singleOrError()
                .subscribeOn(io())
        }

        val s1: Single<String> = createSingle("v1")
        val s2: Single<Int> = Single.just(2)
        // Here, we move the Nullable value outside, so the whole Single<String> is Nullable, and not the value inside the Single`enter code here`
        val s3: Single<String>? = null
        val s4: Single<Int> = Single.just(4)
        val s5: Single<String> = createSingle("v5")
        val s6: Single<String> = createSingle("v6")
        val s7: Single<String> = createSingle("v7")
        val s8: Single<String> = createSingle("v8")
        val s9: Single<String> = createSingle("v9")
        val s10: Single<String>? = null
        val s11 = createSingle("v11")

        //When
        // Here I curry the function, so I can apply one by one the the arguments via zipOver() and preserve the types

        val singleFunction = Single.just(service.curried()).subscribeOn(io())

        val result = singleFunction
            .zipOver(s1)
            .zipOver(s2)
            .zipOverNullable(s3)
            .zipOver(s4)
            .zipOver(s5)
            .zipOver(s6)
            .zipOver(s7)
            .zipOver(s8)
            .zipOver(s9)
            .zipOverNullable(s10)
            .zipOver(s11)
            .flatMap { it }

        //Then
        result
            .test()
            .awaitDone(50, TimeUnit.SECONDS)
            .assertSubscribed()
            .assertValues("Values:v1;2;none;4;v5;v6;v7;v8;v9;none;v11")
    }

    @Test
    fun testMapOver() {
        //Given
        val bothSubscribed = CountDownLatch(1) // Change this value to 2 to run the test slowly
        val subscribeThreadsStillRunning = CountDownLatch(0) // Change this value to 1 to run the test slowly

        val service: (String, Int, String?, Int, String, String, String, String, String, String?, String) -> Single<String> = {
                s1: String,
                s2: Int,
                s3: String?,
                s4: Int,
                s5: String,
                s6: String,
                s7: String,
                s8: String,
                s9: String,
                s10: String?,
                s11: String ->
            val result =
                listOf(s1, "$s2", s3 ?: "none", "$s4", s5, s6, s7, s8, s9, s10 ?: "none", s11).joinToString(separator = ";")
            Single.just("Values:$result")
        }

        val createSingle = { value: String ->
            Observable
                .create<String> { emitter ->
                    println("Parallel subscribe $value on ${Thread.currentThread().name}")
                    bothSubscribed.countDown()
                    subscribeThreadsStillRunning.await(20, TimeUnit.SECONDS)
                    emitter.onNext(value)
                    emitter.onComplete()
                }
                .singleOrError()
                .subscribeOn(io())
        }

        val s1: Single<String> = createSingle("v1")
        val s2: Single<Int> = Single.just(2)
        // Here, we move the Nullable value outside, so the whole Single<String> is Nullable, and not the value inside the Single`enter code here`
        val s3: Single<String>? = null
        val s4: Single<Int> = Single.just(4)
        val s5: Single<String> = createSingle("v5")
        val s6: Single<String> = createSingle("v6")
        val s7: Single<String> = createSingle("v7")
        val s8: Single<String> = createSingle("v8")
        val s9: Single<String> = createSingle("v9")
        val s10: Single<String>? = null
        val s11 = createSingle("v11")

        //When
        // Here I curry the function, so I can apply one by one the the arguments via zipOver() and preserve the types

        val singleFunction = Single.just(service.curried()).subscribeOn(io())

        val result = singleFunction
            .mapOver(s1)
            .mapOver(s2)
            .mapOverNullable(s3)
            .mapOver(s4)
            .mapOver(s5)
            .mapOver(s6)
            .mapOver(s7)
            .mapOver(s8)
            .mapOver(s9)
            .mapOverNullable(s10)
            .mapOver(s11)
            .flatMap { it }

        //Then
        result
            .test()
            .awaitDone(50, TimeUnit.SECONDS)
            .assertSubscribed()
            .assertValues("Values:v1;2;none;4;v5;v6;v7;v8;v9;none;v11")
    }

    @Test
    fun testMixBoth() {
        //Given
        val bothSubscribed = CountDownLatch(0) // Change this value to 2 to run the test slowly
        val subscribeThreadsStillRunning = CountDownLatch(0) // Change this value to 1 to run the test slowly

        val service: (String, Int, String?, Int, String, String, String, String, String, String?, String) -> Single<String> = {
                s1: String,
                s2: Int,
                s3: String?,
                s4: Int,
                s5: String,
                s6: String,
                s7: String,
                s8: String,
                s9: String,
                s10: String?,
                s11: String ->
            val result =
                listOf(s1, "v$s2", s3 ?: "none", "v$s4", s5, s6, s7, s8, s9, s10 ?: "none", s11).joinToString(separator = ";")
            Single.just("Values:$result")
        }

        val createSingle = { value: String ->
            Observable
                .create<String> { emitter ->
                    println("Parallel subscribe $value on ${Thread.currentThread().name}")
                    bothSubscribed.countDown()
                    subscribeThreadsStillRunning.await(5, TimeUnit.SECONDS)
                    emitter.onNext(value)
                    emitter.onComplete()
                }
                .singleOrError()
                .subscribeOn(io())

        }

        val s1: Single<String> = createSingle("v1")
        val s2: Single<Int> = Single.just(2)
        // Here, we move the Nullable value outside, so the whole Single<String> is Nullable, and not the value inside the Single`enter code here`
        val s3: Single<String>? = null
        val s4: Single<Int> = Single.just(4)
        val s5: Single<String> = createSingle("v5")
        val s6: Single<String> = createSingle("v6")
        val s7: Single<String> = createSingle("v7")
        val s8: Single<String> = createSingle("v8")
        val s9: Single<String> = createSingle("v9")
        val s10: Single<String>? = null
        val s11 = createSingle("v11")

        //When
        // Here I curry the function, so I can apply one by one the the arguments via zipOver() and preserve the types

        val singleFunction = Single.just(service.curried()).subscribeOn(io())

        val result = singleFunction
            .zipOver(s1)
            .mapOver(s2)
            .zipOverNullable(s3)
            .zipOver(s4)
            .mapOver(s5)
            .mapOver(s6)
            .mapOver(s7)
            .mapOver(s8)
            .zipOver(s9)
            .zipOverNullable(s10)
            .zipOver(s11)
            .flatMap { it }

        //Then
        result
            .test()
            .awaitDone(50, TimeUnit.SECONDS)
            .assertSubscribed()
            .assertValues("Values:v1;v2;none;v4;v5;v6;v7;v8;v9;none;v11")
    }

}