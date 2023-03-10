package pgdp.minijvm;


import java.util.* ;
public class Debugger {
	// those are necessary  object for this class
	private Instruction[] code = new Instruction[10] ;
	
	private Stack stack;
	
	private int stackSize    ;
	
	private Simulator simulator ; 
	
	// Using two simulatorstucks because one is used to help performing undo command and the other to help performing back command. 
	//First keeps track of every action the other keeps track every execution.  
 
	private SimulatorStack  simulatorstack = new SimulatorStack() , backupp = new SimulatorStack() ;
	
	// Method takes a string as an argument and converts that string into an Instruction object
	public static Instruction  parseInstruction(String str) {
		// used to decide if argument contains only instruction or instruction and the integer 
		String arr[] = str.split(" ") ;
		int k = 0 ;
		
		if (arr.length>1) { 
			try {
				   k = Integer.parseInt(arr[1]);
				}
				catch (NumberFormatException e)
				{
				   k = 0;
				}
		}
		
		if (arr[0].toUpperCase().equals("ADD")) 
			return new Add();
		
		else if (arr[0].toUpperCase().equals("ALLOC"))
			return new Alloc(k);
		
		else if(arr[0].toUpperCase().equals("CONST")) 
			return new  Const(k);
		    
		
		else if(arr[0].toUpperCase().equals("FJUMP")) 
			return new  FJump(k);
		    
		else if (arr[0].toUpperCase().equals("HALT"))
			return new Halt();
		
		else if(arr[0].toUpperCase().equals("JUMP")) 
			return new  Jump(k);
		    
		else if (arr[0].toUpperCase().equals("LESS")) 
			return new Less();
		
		else if(arr[0].toUpperCase().equals("LOAD")) 
			return new  Load(k);
		else if(arr[0].toUpperCase().equals("STORE")) 
			return new  Store(k);
		else if(arr[0].toUpperCase().equals("SUB")) 
			return new  Sub();
		else return new Halt();
		}
		
		
	
	// Constructor that take a array of strings as a instruction argument 
	public Debugger(int stackSize, String[] instructionsAsStrings) {
        stack = new Stack(stackSize);
		this.stackSize = stackSize ;
		
		for (int i = 0 ; instructionsAsStrings[i]!=null ; i++ ) {
			this.code[i] = parseInstruction(instructionsAsStrings[i]) ;
		}
		
		this.simulator= new Simulator(stackSize ,this.code) ; 
		
	}
	
	// Constructor that take a array of instruction class object as a instruction argument 
	public Debugger(int stackSize, Instruction[] code) {
		stack = new Stack(stackSize);
		this.stackSize = stackSize ;
		this.code = code ;
		this.simulator= new Simulator(this.stackSize ,this.code) ; 
		
	}
	
	// Function that perform setBreakpoint command of debugger
	public String setBreakpoint(int index) {
		boolean c = false;  
		Simulator copy = this.simulator.createCopy() ;
		int cs= 0  ; 
		for (Instruction e : code) {
			cs++ ; 
			if(e instanceof Halt || cs==this.simulator.getBreakpoint() ) { c=true  ;
				break ;
			}
		}
		
		if (c) {
			if (index <=cs && index > 0 )  {
				this.simulator.setBreakpoint(index); 
				simulatorstack.push(copy);
				return null ;
				
			}
			else return "Breakpoint already set!\n" ;
				
			}
		else if (index >=this.code.length || index < 0 )  return 	 "Invalid breakpoint index! \n" ;
		else {
			this.simulator.setBreakpoint(index);
			simulatorstack.push(copy);
			return null ;
			}
		
	}
	
	// Function that perform removeBreakpoint command of debugger
	public String removeBreakpoint(int index) {
		Simulator copy = this.simulator.createCopy() ;
		
		if(this.simulator.getBreakpoint()== index) { 
			simulator.setBreakpoint(0);
			simulatorstack.push(copy);
			return null ;
		}
		else if (index >=this.code.length || index < 0 )  return 	 "Invalid breakpoint index! \n" ; 
		else return "No breakpoint to remove!\n" ;
		
	}
	
	// Other way of presenting setBreakpoint and removeBreakpoint function  
	/**              
	public String setBreakpoint(int index) {
		boolean c = false;  
		Simulator copy = this.simulator.createCopy() ;
		int cs= 0  ; 
		for (Instruction e : code) {
			cs++ ; 
			if(e instanceof Halt) { c=true  ;
				break ;
			}
		}
		
		if (c) {
			if (index <=cs) {
				code[index] = new Halt() ;
				this.simulator= new Simulator(this.stackSize ,this.code) ;
				simulatorstack.push(copy);
				return null ;
			}
		
			else return "Breakpoint already set!\n" ;
			
			}
		else if (index >=this.code.length || index < 0 )  return 	 "Invalid breakpoint index! \n" ; 	
		else {
			code[index-1] = new Halt() ;
			this.simulator= new Simulator(this.stackSize ,this.code) ;
			simulatorstack.push(copy);
			return null ;
			}
		
	}
	
	
	public String removeBreakpoint(int index) {
		boolean c = false; 
		Simulator copy = this.simulator.createCopy() ;
		
		for (Instruction e : code) {
			if(e instanceof Halt) c=true ;
		}
		
		if (!(c)) return "No breakpoint to remove!\n" ;
		else if (index >=this.code.length)  return 	 "Invalid breakpoint index! \n" ; 	
		else {
			this.run() ;
			this.simulator.setHalted(false);
			simulatorstack.push(copy);
			return null ;
			}
	} **/
	
	
	//// Function that perform run command of debugger
	public String run() {
		Simulator copy = this.simulator.createCopy() ;
		Simulator copy1 = this.simulator.createCopy() ;
		if (this.simulator.isHalted()) return "No more instructions to execute!\n" ;
		else {
			while (this.simulator.executeNextInstruction()) {
				
				if(this.simulator.getProgramCounter() >= this.simulator.getBreakpoint())
				{copy1 = this.simulator.createCopy() ;
				backupp.push(copy1);
				break ;
				}
			}
			simulatorstack.push(copy);
			return null ;
		}
	}
	
	// Function that perform next command of debugger
	public String next(int k) {
		Simulator copy = this.simulator.createCopy() ;
		Simulator copy1 ;
		if (this.simulator.isHalted() ) return "No more instructions to execute!\n" ; 
		else if (k<0) return "Instruction count must be positive!\n" ;
		else {
			for (int i = 0 ; (i<k ||this.simulator.isHalted() ) ; i++) {
				copy1 = this.simulator.createCopy() ;
				this.simulator.executeNextInstruction() ;
				backupp.push(copy1);
				}
			simulatorstack.push(copy);         
			return null ;
		}
	}
	
	// Function that perform step command of debugger
	public String step() {
		Simulator copy = this.simulator.createCopy() ;
		Simulator copy1 = this.simulator.createCopy() ;
		//backupp.push(copy1);
		if (this.simulator.isHalted() ) return "No more instructions to execute!\n" ; 
		else {
			copy1 = this.simulator.createCopy() ;
			this.simulator.executeNextInstruction() ;
			backupp.push(copy1);
			simulatorstack.push(copy);
			return null ;
		}
	}
	
	// Function that perform reset command of debugger
	public String reset() {
		Simulator copy = this.simulator.createCopy() ;
		simulatorstack.push(copy);
		backupp.clear();
		this.simulator= new Simulator(stackSize ,this.code)  ;
		return null ;
	}
	
	 
	// Function that perform back command of debugger
	public String back() {
		Simulator copy = this.simulator.createCopy() ;
		
		if (backupp.isEmpty()) return "Cannot go back an instruction, none left!\n" + this.simulator;
		else {
			simulator = backupp.pop() ;
			simulatorstack.push(copy);
			return null ;
		}
	}
	
	
	// Function that perform undo command of debugger
	
	public String undo() {
		Simulator copy = this.simulator.createCopy() ;
		
		if (simulatorstack.isEmpty()) return "No debugger command to undo!\n" + this.simulator ;
		else {
			simulator = simulatorstack.pop() ;
			simulatorstack.push(copy);
			return null ;
		}
	}
	
	// Function that return simulator of debugger class
	public Simulator getSimulator() {
		return this.simulator ;
	}
	
	
	//Function that make possible execution of other debugger function just using string commands as indicator 
	public String  executeDebuggerCommand(String command) {
		String arr[] = command.split(" ") ,s ;
		int i;
		
		switch (arr[0].toUpperCase()) {
		
		case "SET-BREAKPOINT" :
			try {
				   i = Integer.parseInt(arr[1]);
				}
				catch (NumberFormatException e)
				{
				   i = 0;
				}
			s=this.setBreakpoint(i) ;  break ;
			
		case "REMOVE-BREAKPOINT" :
			try {
				   i = Integer.parseInt(arr[1]);
				}
				catch (NumberFormatException e)
				{
				   i = 0;
				}
			s=this.removeBreakpoint(i) ; break ;
		
	     case "RUN" :
	    	 s=this.run() ;  break ;
	     	 
	     case "NEXT" :
	    	 try {
				   i = Integer.parseInt(arr[1]);
				}
				catch (NumberFormatException e)
				{
				   i = 0;
				}
	    	 s=this.next(i) ; break ;
	    	 
	     case "STEP" :
	    	 s=this.step() ; break ;
	    	 
	     case "BACK" :
	    	 s=this.back() ; break ;
	    
	     case "RESET" :
	    	 s=this.reset() ; break ;
	    	 
	     case "UNDO" : 
	    	 s=this.undo() ;  break ;
	    
	     default : s = "Unknown debugger command!\n" ;
	
		}
		
		return s ;
	}
	
	
	// This is the main function that make possible input from console 
	public static void main(String[] args) {
	
		Scanner input = new Scanner (System.in) ;
		int i = 0 ; 
		String [] instructionStr = new String [10   ] ;
		
		
		String in , str  ; 
		
				
		System.out.print("Please enter the next instruction or press Enter to complete the input:\n> ");
		in  = input.nextLine() ; 
		
		while (!in.equals("")) {
			
			instructionStr[i]= in; 
			System.out.print("Please enter the next instruction or press Enter to complete the input:\n> ");
			in  = input.nextLine() ; 
			i++;
			
		}
		
		
		
		System.out.print("Please enter the stack size for the MiniJVM:\n> ");
		int stacksize  =  input.nextInt() ;
		in  = input.nextLine() ;
		Debugger debugger = new Debugger(stacksize,instructionStr ) ; 
		System.out.print(debugger.getSimulator());
		
		
		System.out.print("Input debugger command:\n> ");
		in  = input.nextLine() ;
		
		while (!in.toUpperCase().equals("EXIT")) {
			str = debugger.executeDebuggerCommand(in) ;
			if(str == null) System.out.print(debugger.getSimulator());
			else System.out.print(str);
			System.out.print("Input debugger command:\n> ");
			in  = input.nextLine() ; 
			
		} 
		
		input.close();
	}
	
}
