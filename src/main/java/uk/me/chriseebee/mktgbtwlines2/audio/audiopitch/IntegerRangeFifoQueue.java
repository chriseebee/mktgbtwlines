package uk.me.chriseebee.audiopitchjava;

import java.util.Arrays;

public class IntegerRangeFifoQueue implements Queue<Integer> {

    protected int[] ring;
    protected int index;
    private int maMin;
    private int maMax;
    
    /**
    * @param initialValues contains the ring's initial values.
    * The "oldest" value in the queue is expected to reside in
    * position 0, the newest one in position length-1.
    */
    public IntegerRangeFifoQueue(int initialLength) {
        // This is a little ugly, but there are no
        // generic arrays in Java
        ring = new int[initialLength];
        
        Arrays.fill(ring,0);
       
        // The next time we add something to the queue,
        // the oldest element should be replaced
        index = 0;
        maMin = 0;
        maMax = 0;
    }

    public boolean add(Integer newest) {
        return offer(newest);
    }

    public Integer element() {
        return ring[getHeadIndex()];
    }
    
    public Integer getRange() {
    	return maMax - maMin;
    }

    public boolean offer(Integer newest) {
        //int oldest = ring[index];
        ring[index] = newest;
        incrIndex();
        calcRange();
        return true;      
    }
    
    private void calcRange() {
    	int[] tempArray = ring.clone();
    	Arrays.sort(tempArray);
    	maMin = tempArray[0];
    	maMax = tempArray[tempArray.length-1];
    }

    public Integer peek() {
        return new Integer(ring[getHeadIndex()]);
    }

    
    public Integer poll() {
        throw new IllegalStateException("The poll method is not available for NumberFixedLengthFifoQueue.");
    }

    
    public Integer remove() {
        throw new IllegalStateException("The remove method is not available for NumberFixedLengthFifoQueue.");
    }

    
    public Integer get(int absIndex) throws IndexOutOfBoundsException {
        if (absIndex >= ring.length) {
            throw new IndexOutOfBoundsException("Invalid index " + absIndex);
        }
        int i = index + absIndex;
        if (i >= ring.length) {
            i -= ring.length;
        }
        return ring[i];
    }
    
    
    public String toString() {
        StringBuffer sb = new StringBuffer("[");
        for (int i = index, n = 0; n < ring.length; i = nextIndex(i), n++) {
            sb.append(ring[i]);
            if (n+1 < ring.length) { sb.append(", "); } 
        }
        return sb.append("]").toString();
    }
    
    public int getAverage() {
    	if (ring.length==0) { return 1; }
        int total = 1;
        for (int i = index, n = 0; n < ring.length; i = nextIndex(i), n++) {
            total = total + ring[i];
        }
        if ((int)(total/ring.length)==0) { return 1; }
        return total/ring.length;
    }
    
    protected void incrIndex() {
        index = nextIndex(index);
    }
    
    protected int nextIndex(int current) {
        if (current + 1 >= ring.length) { return 0; }
        else return current + 1;
    }

    protected int previousIndex(int current) {
        if (current - 1 < 0) { return ring.length - 1; }
        else return current - 1;
    }
    
    protected int getHeadIndex() {
        if (index == 0) { return ring.length-1; }
        else return index-1;
    }

//    public static void main (String[] args) {
//    	IntegerRangeFifoQueue q = new IntegerRangeFifoQueue(10);
//    	q.add(new Integer(12));
//    	q.add(new Integer(20));
//    	q.add(new Integer(8));
//    	q.add(new Integer(8));
//    	q.add(new Integer(8));
//    	
//    	System.out.println("Range (12)= "+q.getRange());
//    	
//    	q.add(new Integer(22));
//    	q.add(new Integer(20));
//    	q.add(new Integer(8));
//    	q.add(new Integer(8));
//    	q.add(new Integer(8));
//    	
//    	System.out.println("Range (14)= "+q.getRange());
//    	
//    	q.add(new Integer(8));
//    	q.add(new Integer(8));
//    	q.add(new Integer(8));
//    	q.add(new Integer(8));
//    	q.add(new Integer(8));
//    	
//    	System.out.println("Range (14)= "+q.getRange());
//    	
//    	q.add(new Integer(8));
//    	q.add(new Integer(8));
//    	q.add(new Integer(8));
//    	q.add(new Integer(8));
//    	q.add(new Integer(10));
//    	
//    	System.out.println("Range (2)= "+q.getRange());
//    	
//    }
}