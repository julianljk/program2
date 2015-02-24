/***************************************************************************************
  PlayerImpl.java
  Implement five functions in this file.
  ---------
  Licensing Information:  You are free to use or extend these projects for
  educational purposes provided that (1) you do not distribute or publish
  solutions, (2) you retain this notice, and (3) you provide clear
  attribution to UW-Madison.

  Attribution Information: The Take Stone Games was developed at UW-Madison.
  The initial project was developed by Jerry(jerryzhu@cs.wisc.edu) and his TAs.
  Current version with depthLimit and SBE was developed by Fengan Li(fengan@cs.wisc.edu)
  and Chuck Dyer(dyer@cs.wisc.edu)

 *****************************************************************************************/

import java.util.*;

public class PlayerImpl implements Player {
	// Identifies the player
	private int name = 0;
	int n = 0;


	// Constructor
	public PlayerImpl(int name, int n) {
		this.name = 0;
		this.n = n;
	}
	/*
	 * 
	 * */
	// Function to find possible successors
	@Override
	public ArrayList<Integer> generateSuccessors(int lastMove, int[] takenList) //this method should be fine
	{
		ArrayList <Integer> myList = new ArrayList <Integer>();
		if (lastMove == -1) //must choose an odd-numbered stone that is less than n/2
		{
			for (int i = ((takenList.length - 2)/2); i > 0; i--)
			{
				if (i % 2 != 0 && takenList[i] == 0)
				{
					myList.add(i);
				}
			}
		}
		else
		{
			for (int i = takenList.length - 1; i > 0; i--)
			{ 
				if (takenList[i] == 0 && (i % lastMove == 0 || lastMove % i == 0))
				{
					myList.add(i);
				}
			}
		}
		return myList;

	}

	// The max value function
	@Override
	public double max_value(GameState s, int depthLimit) 
	{
		double stateEvaluationValue = stateEvaluator(s);

		if (depthLimit == 0)
		{
			return stateEvaluationValue;
		}
		else
		{
			double currMax = Double.NEGATIVE_INFINITY;
			int bestMove = 0;
			ArrayList <Integer> succList = generateSuccessors(s.lastMove, s.takenList);
			if (succList.isEmpty())
			{
				s.bestMove = -1;
				s.leaf = true;
				return -1;
			}
			Iterator <Integer> myItr = succList.iterator();
			while (myItr.hasNext())
			{
				double tempMax = currMax;
				int currNumber = myItr.next();
				int [] newList = s.takenList;
				newList [currNumber] = 1; //assign it to player 1
				GameState newGameState = new GameState(newList, currNumber);
				currMax = Math.max(currMax, min_value(newGameState, depthLimit - 1));

				if (tempMax < currMax)
				{
					bestMove = currNumber;
				}
			}
			s.bestMove = bestMove;			
			return currMax;
		}


	}

	// The min value function
	@Override
	public double min_value(GameState s, int depthLimit)
	{
		double stateEvaluationValue = stateEvaluator(s);
	
		if (depthLimit == 0)
		{
			return -(stateEvaluationValue);
		}
		else
		{
			double currMin = Double.POSITIVE_INFINITY;
			int bestMove = 0;
			ArrayList <Integer> succList = generateSuccessors(s.lastMove, s.takenList);
			if (succList.isEmpty())
			{
				s.leaf = true;
				return 1;
			}
			Iterator <Integer> myItr = succList.iterator();
			
			while (myItr.hasNext())
			{
				int currNumber = myItr.next();
				
				double tempMin = currMin;
				int [] newList = s.takenList;
				newList [currNumber] = 2;
				GameState newGameState = new GameState(newList, currNumber);
				currMin = Math.min(currMin, max_value(newGameState, depthLimit - 1));

				if (currMin < tempMin)
				{
					bestMove = currNumber;
				}
			}
			s.bestMove = bestMove;
			return currMin;

		}
	}

	// Function to find the next best move
	@Override
	public int move(int lastMove, int[] takenList, int depthLimit) 
	{
		GameState myState = new GameState(takenList, lastMove);
		double maxValue = max_value(myState, depthLimit);
		int myBestMove = myState.bestMove;
		if (myState.leaf) //why did i do this
		{
			return -1;
		}
		return myBestMove;
	}

	// The static board evaluator function
	@Override
	public double stateEvaluator(GameState s)
	{
		int count = 0;
		if (s.takenList[1] == 0)
		{
			return 0;
		}
		else if(s.lastMove == 1)
		{
			for (int i = 1; i < s.takenList.length ; i++)
			{
				if (s.takenList[i] == 0)
				{
					count++;
				}
			}
			if (count % 2 == 0)
			{
				return -0.5;
			}
			else 
			{
				return 0.5;
			}
		}
		else if (isPrime(s.lastMove))
		{
			for (int i = 1; i < s.takenList.length; i++)
			{
				if (s.takenList[i] == 0 && i % s.lastMove == 0) // i = 4, % 2
				{
					count++;
				}
			}
			if (count % 2 == 0)
			{
				return -0.7;
			}
			else
			{
				return 0.7;
			}
		}
		else 
		{
			int largestPrime = 2;
			for (int i = 2; i < (s.lastMove/2); i++)
			{
				if (s.lastMove % i == 0 && isPrime(i))
				{
					largestPrime = i;
				}
			}
			for (int i = largestPrime; i <= s.lastMove; i+= largestPrime)
			{
				if (s.takenList[i] == 0)
				{
					count++;
				}
			}
			if (count % 2 == 0)
			{
				return -0.6;
			}
			else 
			{
				return 0.6;
			}

		}
	}
	private boolean isPrime(int n)
	{
		for (int i = 2 ; i <= Math.sqrt(n) ; i++)
		{
			if ( n % i == 0 )
			{
				return false;
			}
		}
		return true;
	}
}
