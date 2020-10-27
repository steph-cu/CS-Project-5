
// QuadraticProbing Hash table class
//
// CONSTRUCTION: an approximate initial size or default of 101
//
// ******************PUBLIC OPERATIONS*********************
// bool insert( x )       --> Insert x
// bool remove( x )       --> Remove x
// bool contains( x )     --> Return true if x is present
// void makeEmpty( )      --> Remove all items (with doClear())

// String toString( limit ) -> prints filled index and number in index
// void rehash() 				-> makes doubled size table 
// int findPos(x) 				-> gets the position it should be in (doesn't do well with removal)
// int size()					-> returns currently active entries
// int capcity() 				-> returns the size of array
// E find(x) 					-> returns null if not in there, returns the number if in there 
// bool isActive(poistion) 		-> return whether the position is active or not
// int myhash(x) 				-> makes a hash for the element
// void AllocateArray(size) 	-> makes new array of this size
import java.util.*;
import java.io.File;

/**
 * Probing table implementation of hash tables.
 * Note that all "matching" is based on the equals method.
 * @author Mark Allen Weiss
 */
public class HashTable<E extends Comparable<? super E>>
{
    /**
     * Construct the hash table.
     */
    public HashTable( )
    {
        this( DEFAULT_TABLE_SIZE, DEFAULT_TABLE_SIZE2 );// 101, 157
    }

    /**
     * Construct the hash table.
     * @param size the approximate initial size.
     */
    public HashTable( int size, int size2 )
    {
        allocateArray( size, size2 );
        doClear( );
    }

    /**
     * Insert into the hash table. If the item is
     * already present, do nothing.
     * Implementation issue: This routine doesn't allow you to use a lazily deleted location.  Do you see why?
     * @param x the item to insert.
     */
  
    private int myhash( E x )// i decided to keep these this way sue me
    {
        int hashVal = x.hashCode( );

        hashVal %= array.length;
        if( hashVal < 0 )
            hashVal += array.length;

        return hashVal;
    }
	private int myhash2(E x){
		int hashVal = x.hashCode();
		
		hashVal %= array2.length;
		if( hashVal < 0) 
			hashVal += array2.length;
		
		return hashVal;
	}
	private int findPos(E x, int arrayNum){// finds where it needs to be put 
		if (arrayNum == 1) return myhash(x);
		else return myhash2(x);
	}
	public E getPos(E x, int arrayNum){// gets the thing that your searching for 
		if (arrayNum == 1) return array[findPos(x, 1)].element;
		else return array2[findPos(x, 2)].element;
	}
	public E get(int x, int arrayNum){// finds a certain part in the array 
		if (arrayNum == 1) return array[x].element;
		else return array2[x].element;
	}
	public int ArrayLength(int arrayNum){// returns array length
		if (arrayNum == 1) return array.length;
		else return array2.length;
		//return array.length;
	}
	
	
	
	public boolean insert(E x){
		int possiblePos = findPos(x, 1);// where it goes in first table
		int possiblePos2 = findPos(x, 2); // where it goes in second table
		insertCt++;// increases number of times we've used insert()
		if (array[possiblePos] != null && array[possiblePos].element.compareTo(x) == 0){// if the place isn't null and it isn't the same thing
			return false;// returns false
		}
		if (array2[possiblePos2] != null && array2[possiblePos2].element.compareTo(x) == 0){
			return false;
		}
		if (!isActive(possiblePos, 1)){// if place 1 is empty  put it in there
			array[possiblePos] = new HashEntry<>(x, true);
			currentActiveEntries++;// increases number of cities in the first one 
			return true;
		}
		else if(!isActive(possiblePos2, 2)){// if place 2 is empty
			array2[possiblePos2] = new HashEntry<>(x, true);
			currentActiveEntries2++;// inc numb in 2nd one 
			return true;
		}
		else{
			HashEntry<E> hold = array[possiblePos];// will take what's in this position
			array[possiblePos] = new HashEntry<>(x, true);// puts new one in
			return replant(hold, 2);//we'll replant the new one and return if it got planted
			//return true
		} 
	}
	
	private boolean replant(HashEntry<E> hold, int arrayNum){// goes and replants things that have been pushed into other table
		insertProbeCt++;// added amount of times we needed to probe with insert 
		int otherPos = findPos(hold.element, arrayNum);// checks other position
		if (++replanting > REPLANT_MAX){// if we've made a big cycle lets recycle
			return rehash(hold);
			//return;
		}
		if (!isActive(otherPos, arrayNum)){ //if its empty we'll put 
			currentActiveEntries++;
			if (arrayNum == 1) array[otherPos] = hold;
			else array2[otherPos] = hold;
			replanting = 0;
			return true;
		}
		if (arrayNum == 1){ 
			HashEntry<E> held = array[otherPos];
			array[otherPos] = hold;
			return replant(held, 2);
		}
		else{
			HashEntry<E> held = array2[otherPos];
			array2[otherPos] = hold;
			return replant(held, 1);
		}
	}
	 
    /**
     * Expand the hash table.
     */
    private boolean rehash(HashEntry<E> held)// rehashes
    {
		rehashing++;
		boolean first = false, second = false;
        HashEntry<E> [ ] oldArray = array;
		HashEntry<E> [ ] oldArray2 = array2; 
		

        // Create a new double-sized, empty table
        allocateArray(2 * oldArray.length, 2 * oldArray2.length );
        currentActiveEntries = 0;
		currentActiveEntries2 = 0;

        // Copy tables over
		insert(held.element);
        for( HashEntry<E> entry : oldArray )
            if( entry != null && entry.isActive )
                first = insert( entry.element );
		for( HashEntry<E> entry : oldArray2 )
            if( entry != null && entry.isActive )
                second = insert( entry.element );
		return first && second;
    }
	
    public String printTable (int limit, String file){
        StringBuilder sb = new StringBuilder();
        int ct=0;
		System.out.println("File " + file + " Misspelled Words");
		// currentActiveEntries needs a second one for keeping track of both charts
		sb.append("table1 (" + currentActiveEntries + "/" + array.length + ") = " + currentActiveEntries/array.length + "\n");
        for (int i=0; i < array.length && ct < limit; i++){// while less than the string and ct less than limit
            if (array[i]!=null && array[i].isActive) {// if the array  spot isn't null and has a number there
                sb.append( i + ": " + array[i].element.toString() + "\n" );// print the number there
                ct++;// increase numbef of ct's printed
            }
        }
		ct = 0;
		sb.append("table2 (" + currentActiveEntries2 + "/" + array2.length + ") = " + currentActiveEntries/array2.length +"\n");
        for (int i=0; i < array2.length && ct < limit; i++){// while less than the string and ct less than limit
            if (array2[i]!=null && array2[i].isActive) {// if the array  spot isn't null and has a number there
                sb.append( i + ": " + array2[i].element.toString() + "\n" );// print the number there
                ct++;// increase numbef of ct's printed
            }
        }
		sb.append("insertCoutn " + insertCt + " probe count + " + (insertCt + insertProbeCt) + " probes/insert " +((insertCt + insertProbeCt)/insertCt)+ " rehashCount " + rehashing );
		return sb.toString();
    }
	
    /**
     * Remove from the hash table.
     * @param x the item to remove.
     * @return true if item removed
     */
    public boolean remove( E x )// ignore
    {
        int currentPos = findPos( x, 1 );
        if( isActive( currentPos, 1 ) )
        {
            array[ currentPos ].isActive = false;
            currentActiveEntries--;
            return true;
        }
        else
            return false;
    }

    /**
     * Get current size.
     * @return the size.
     */
    public int size( )// ignore
    {
        return currentActiveEntries;
    }

    /**
     * Get length of internal table.
     * @return the size.
     */
    public int capacity( )// ignore
    {
        return array.length;
    }

    /**
     * Find an item in the hash table.
     * @param x the item to search for.
     * @return true if item is found
     */
    public boolean contains( E x )// should return true if it's in the arrays
    {
        int currentPos = findPos( x, 1 );
		int currentPos2 = findPos( x, 2 );
        //return isActive( currentPos ) || isActive2( currentPos2 );
		return (array[currentPos].element.compareTo(x) == 0) || (array2[currentPos2].element.compareTo(x) == 0);
    }

    /**
     * Find an item in the hash table.
     * @param x the item to search for.
     * @return the matching item.
     */
    public E find( E x )// ignore
    {
        int currentPos = findPos( x, 1 );
        if (!isActive( currentPos, 1 )) {
            return null;
        }
        else {
            return array[currentPos].element;
        }
    }

    /**
     * Return true if currentPos exists and is active.
     * @param currentPos the result of a call to findPos.
     * @return true if currentPos is active.
     */
    public boolean isActive( int currentPos, int arrayNum )// if the position in the array is active returns true 
    {
		if (arrayNum == 1) return array[currentPos] != null && array[currentPos].isActive;
		else return array2[ currentPos ] != null && array2[ currentPos ].isActive;
    }

    /**
     * Make the hash table logically empty.
     */
    public void makeEmpty( )//ignore
    {
        doClear( );
    }

    private void doClear( )//ignore
    {
        for( int i = 0; i < array.length; i++ )
            array[ i ] = null;
    }


    private static class HashEntry<E>
    {
        public E  element;   // the element
        public boolean isActive;  // false if marked deleted

        public HashEntry( E e )
        {
            this( e, true );
        }

        public HashEntry( E e, boolean i )
        {
            element  = e;
            isActive = i;
        }
		
		
    }

    private static final int DEFAULT_TABLE_SIZE = 10001;
	private static final int DEFAULT_TABLE_SIZE2 = 10057;
	private static final int REPLANT_MAX = 1000;

	private HashEntry<E> [ ] array2; // 2nd table
    private HashEntry<E> [ ] array; // The array of elements
	
    private int currentActiveEntries = 0; 	// Current size
	private int currentActiveEntries2 = 0;
	private int insertProbeCt = 0;
	private int insertCt = 0;
	private int replanting = 0; 
	private int rehashing = 0;

    /**
     * Internal method to allocate array.
     * @param arraySize the size of the array.
     */
    private void allocateArray( int arraySize, int arraySize2 )
    {
        array = new HashEntry[  arraySize  ];
		array2 = new HashEntry[  arraySize2 ];
    }

	/*
	- both tables use same hash function - x
	- size of tables differ - x
	- rehash after x attempts on each table - x(half of respective arrays)
	- hashtables should print themselves (limit of x occupied entries)
		* print occupied count / capacity
		* total number of inserts (actual inserts it took to do them)
		* print # of rehashes
	-findPos() = finds current location of item || determines where the item should be placed
		* might make into 2 functions - x
	-isActive might be for whether an element is there
	- occupiedCt is for how many areas have ever held an element
	- WordFreq may need to declare toString(), equals, and hashcode
	- compare # values inserted/ # insert requests
	
	Questions
	- how many times to allow values to be pushed back and forth before rehash
	- do you switch the table to start in?
	- initial sizes?
	
	What we've got right now
	- looks like a linear probing method (old)
	- inserting and "replanting" seem to look fine
	*/

	public static void main( String [ ] args ) 
	{
		
		HashTable<String> dicTable = new HashTable<>();
		ArrayList<String> dicList = new ArrayList<String>();
		int counting = 0;
		File dictionary = new File("dictionary.txt");//gets file
		try{
			//System.out.println("hits dictionary try" );
			Scanner dic = new Scanner(dictionary);
			String dicLine;
			String[] dicW;
			while(dic.hasNextLine()){// inserts into Table
				dicLine = dic.nextLine();
				dicLine = dicLine.toLowerCase().replaceAll("\\p{Punct}","");// gets rid of punctuation and uppercase
				dicW = dicLine.split(" ");// slits the line into words 
				for (String dicWord: dicW){
					//counting++;
					//System.out.println("\n"+dicWords);
					dicTable.insert(dicWord);
					dicList.add(dicWord);
				}
			}
		}
		catch (Exception e){}
		System.out.println("This is from File: dictionary.txt: \n" );
		System.out.println(dicTable.printTable(10, "dictionary.txt"));
		
		String[] paragraphs = {"p.txt", "paragraph1.txt", "paragraph2.txt", "paragraph3.txt"};
		for (int i = 0; i < 4; i++){
			HashTable<WordFreq> misSpelled = new HashTable<>();
			System.out.println("This is from File: " + paragraphs[i] + ": \n" );
			try{
				//System.out.println("made it into try");
				Scanner para = new Scanner(new File(paragraphs[i]));
				String paraLine;
				String[] paraW;
			
				// finds word in dictionary
				while(para.hasNextLine()){// gets the line
					paraLine = para.nextLine();// puts line into string
					paraLine = paraLine.toLowerCase().replaceAll("\\p{Punct}","");// gets rid of punctuation and uppercase
					paraW = paraLine.split(" ");// splits the line into words 
					for (String paraWord: paraW){
						if (!dicTable.contains(paraWord)){// if word isn't in dictionary
							counting++;
							WordFreq newWord = new WordFreq(paraWord);
							if (!misSpelled.insert(newWord)){// if word is already in there 
								if (misSpelled.getPos(newWord, 1).compareTo(newWord) == 0) misSpelled.getPos(newWord, 1).incFreq();
								else misSpelled.getPos(newWord, 2).incFreq();
							}
						}
					}
				}
			}
			catch(Exception e){}
			System.out.println(counting);
			System.out.println(misSpelled.printTable(20, paragraphs[i] ));
			for (int x = 0; x < misSpelled.ArrayLength(1); x++){// these print out the close matching cases
				if (misSpelled.isActive(x, 1)) misSpelled.get(x, 1).findClose(dicList);//needs to get each thing in area
				System.out.println(" - " + misSpelled.get(x, 1).word +"("+ misSpelled.get(x, 1).freq + "):");
				misSpelled.get(x, 1).mCase.printMatches();
			}
			for (int y = 0; y < misSpelled.ArrayLength(2); y++){
				if (misSpelled.isActive(y, 2)) misSpelled.get(y, 2).findClose(dicList);
				System.out.println(" - " + misSpelled.get(y, 2).word +"("+ misSpelled.get(y, 2).freq + "):");
				misSpelled.get(y, 2).mCase.printMatches();
			}	
		}
	}

}
