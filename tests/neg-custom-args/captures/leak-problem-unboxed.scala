import language.experimental.captureChecking
import caps.use

// Some capabilities that should be used locally
trait Async:
  //  some method
  def read(): Unit
def usingAsync[X](op: Async^ => X): X = ???

case class Box[+T](get: T)

def useBoxedAsync(@use x: Box[Async^]): Unit =
  val t0 = x
  val t1 = t0.get // ok
  t1.read()

def useBoxedAsync1(@use x: Box[Async^]): Unit = x.get.read() // ok

def test(): Unit =

  val f: Box[Async^] => Unit = (x:  Box[Async^]) => useBoxedAsync(x) // error
  val t1: Box[Async^] => Unit = useBoxedAsync(_) // error
  val t2: Box[Async^] => Unit = useBoxedAsync // error
  val t3 = useBoxedAsync(_) // was error, now ok
  val t4 = useBoxedAsync // was error, now ok

  def boom(x: Async^): () ->{f} Unit =
    () => f(Box(x))

  val leaked = usingAsync[() ->{f} Unit](boom)

  leaked()  // scope violation
