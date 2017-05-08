# federal-reserve-statement-info-extractor

Our group proposal is to write a code that will examine the text of the Federal Open Market’s
Committee post meeting communique. The website with all of the present and past communication is:
https://www.federalreserve.gov/monetarypolicy/fomccalendars.htm

FOMC statements are impactful and market moving. There is much information to extract from the
statement, such as sentiment of the federal reserve’s view on economic growth and inflation or the
choice the open markets committee made for the base interest rate. 

Feedback from the Professor:

```
Looks interesting but it's quite a mix of tasks, including sentiment analysis, information extraction, and 
some unspecified text comparison.  

The extraction is a substantial job in itself, so you probably will want to cut back on the total number of tasks.
```

The following ideas are:

- Unspecified Text Comparison: 
```
Write code to create a “what is different” from the previous statement to the current statement, similar to WSJ
```

- Information Extraction:

   
```
Train a tagging and information extraction program on different interest rate regimes and check the results 
when passing documents from a different interest rate regime 

```

```
Extract information on the base interest rate and use pattern matching to find out the target fed funds 
rate from the statement from 2006 to present.  Compare the results of a naive information extractor to a MaxEnt tagger.
```

Possible Method: Utilize MaxEnt

```
Work Flow:
1) After getting the statements into place, select statements for a training corpus
2) Part of Speech Tag the trainign corpus and the statements
3) Apply our own TAGS to the training corpus
4) Use MaxEnt to tag the 'test' statements and see if it will identify the correct base rate (and associated changes)
```

Develop a tagging system (our own), and identify number values and action words, for example:

    O (other)
    N (number value)
    A (action)
    U (unit)

    In  O
    view  O
    of  O
    realized  O
    and O
    expected  O
    labor O
    market  O
    conditions  O
    and O
    inflation O
    , O
    the O
    Committee O
    decided A
    to  O
    raise A
    the O
    target  O
    range O
    for O
    the O
    federal O
    funds O
    rate  O
    to  O
    3/4 N
    to  N
    1 N
    percent U
    . O

I can mark a training corpus as well. 


- Naive Bayes:
```
Utilize naïve bayes to see if we can train a program to detect whether a statement indicates
whether the Fed is more likely to raise or lower or keep interest rates the same?
```
The set of equations would be: 
```
P(Neutral or Not Neutral) 
P(Raise Rates | Not Neutral) 
P(Lower Rates | Note Neutral)
```

Other ideas:

4. Explore applying word sense disambiguation to FOMC statements and examine if WSD results are different among statements where there are changes in the base rate or policy

5. Create a tool that uses the CONLL database and name tagging tool to identify voters of FOMC committee and devleop a program that will be able to track a voter's history


