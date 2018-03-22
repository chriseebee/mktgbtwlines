package uk.me.chriseebee.audiopitchjava;

public class ButtonServerTester {

    static final long RECORD_TIME = 60*1000*5;  // 5 minutes
    
	public static void main(String[] args) {
		
        Thread stopper = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(RECORD_TIME);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
               
            }
        });
        
        stopper.start();
        
        ButtonSocketServer bss = new ButtonSocketServer();
        Thread t1 = new Thread (bss);
        t1.start();
	}
}
