package pgdp.minijvm;

public class Simulator {

	private Instruction[] code;
	private int programCounter = 0;
     
	private Stack stack;
	// breakpoint in needed in debugger class later 
	private int stackSize , breakpoint=0 ;
	private boolean halted;
	

	/**
	 * Erstellt einen Simulator mit der Stackgröße {@code stackSize} und dem
	 * MiniJava-Code {@code code}.
	 *
	 * @param stackSize
	 * @param code
	 */
	public Simulator(int stackSize, Instruction[] code) {
		stack = new Stack(stackSize);
		this.code = code;
		this.stackSize= stackSize ;
	}

	public boolean executeNextInstruction() {
		if (halted) {
			return false;
		}
		Instruction instr = code[programCounter];
		programCounter++;
		instr.execute(this);
		return !halted;
	}

	/**
	 * Liefert den Stack des Simulators.
	 */
	public Stack getStack() {
		return stack;
	}

	/**
	 * Setzt den Programmzähler des Simulators auf den übergebenen Wert.
	 *
	 * @param programCounter Der neue Wert des Programmzählers.
	 */
	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}

	/**
	 * Liefert den Wert des Programmzählers des Simulators.
	 */
	public int getProgramCounter() {
		return programCounter;
	}

	/**
	 * Setzt das {@code halted}-Attribut
	 *
	 * @param halted Der neue Wert des Attribus.
	 */
	public void setHalted(boolean halted) {
		this.halted = halted;
	}

	/**
	 * Liefert den Wert des {@code halted}-Attributs.
	 */
	public boolean isHalted() {
		return halted;
	}

	@Override
	public String toString() {
		return String.format("Halted: %b%nProgram counter: %d%n%s%n", halted, programCounter, stack);
	}
	
	// Used to change value of breakpoint 
	public void setBreakpoint(int breakpoint) {
		this.breakpoint= breakpoint ;
	}
	// Used to return value of breakpoint 
	public int getBreakpoint() {
		return this.breakpoint ;
	}
	
	
	// Create a copy of object 
	public Simulator createCopy() {
		Simulator temp = new Simulator (this.stackSize , this.code) ;
		for (int i = 0 ; i<this.getProgramCounter() ; i++) temp.executeNextInstruction() ;
	   return temp ;
	}
}
