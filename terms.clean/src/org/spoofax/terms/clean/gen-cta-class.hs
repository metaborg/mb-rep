{- I'm not sure if I want to use this script to generate these specialised classes for different size constructors... -}
import System.Environment
import System.IO
import Control.Exception.Base
import Data.Traversable
import Data.List

main = do
  [intStr] <- getArgs
  let int = read intStr :: Int
  assert (int > 0) (return ())
  let className = concat ["AbstractCTA", show int]
  let fileName = concat [className, ".java"]
  let fileContents = unlines
        [ "package org.spoofax.terms.clean;"
        , ""
        , "import org.immutables.value.Value;"
        , ""
        , "import java.util.List;"
        , ""
        , "@Value.Immutable"
        , "abstract class " ++ className ++ "<"
        , customRepeat'
            int
            (\n -> "        T" ++ n ++ " extends ICleanTerm")
            ","
            "> implements ICleanTerm {"
        , "    public static final TermKind termKind = TermKind.Application;"
        , "    public abstract String constructor();"
        , customRepeat
            int
            (\n -> concat ["    public abstract T", n, " child_", n, "();"])
        , ""
        , "    public abstract ICleanTerm[] annotations();"
        , ""
        , "    @Override"
        , "    public TermKind getTermKind() {"
        , "        return termKind;"
        , "    }"
        , "}"
        ]
  writeFile fileName fileContents

customRepeat :: Int -> (String -> String) -> String
customRepeat n f = concat $ intersperse "\n" $ map (f . show) [1..n]

customRepeat' :: Int -> (String -> String) -> String -> String -> String
customRepeat' n f a b =
    if n > 1
        then concat $ intersperse "\n" $ (map ((++ a) . f . show) [1 .. n-1]) ++ [ last ]
        else last
    where last = f (show n) ++ b
