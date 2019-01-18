package test4repair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonAPI;
import spoon.SpoonModelBuilder.InputType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

public class App {

	private static final String M_ISO = "M_ISO";
	private static final String B_ISO = "B_ISO";
	private static final String L_ISO = "L_ISO";
	
//	String project;
//	
//	String bugId; 
//	String level; //M_ISO;B_ISO;L_ISO
//	String srcpath;
//	String testpath; 
//	String srcClass; 
//	String testClass; 
//	String buggyMethod;
//	String buggyline;
//	List failtests= new ArrayList();
	
	
public static void main(String[] args) {
	System.out.println("Starting Read Configurations:");
	
	String project = args[0];
	String bugId = args[1];
	String level = args[2]; //M_ISO;B_ISO;L_ISO
	String srcpath = args[3];
	String testpath = args[4];
	String srcClass="org.jfree.chart.renderer.category.AbstractCategoryItemRenderer"; 
	String buggySrcMethod="getLegendItems"; 
	String testClass=""; 
	String buggyMethod = "";
	String buggyline="";
	List failtests= new ArrayList();
	
	System.out.println("project:"+project);
	System.out.println("bugId:"+bugId);
	System.out.println("level:"+level);
	
	srcpath="./defects4j-project/"+project+"_"+bugId+"_buggy/"+srcpath;
	testpath="./defects4j-project/"+project+"_"+bugId+"_buggy/"+testpath;
	
	//generate different level isolated source files
	sourceIsolation(srcpath,srcClass,level,buggySrcMethod);


}


private static void sourceIsolation(String srcpath,String srcClass,String level, String buggySrcMethod) {
	srcClass.replace('.', '/');
	String output1 =srcpath+""+level;
	String output2 ="./";
	File outputSrcDirectory = new File(output1);
	
	
	final SpoonAPI spoon = new Launcher();
	spoon.addInputResource(srcpath);
	spoon.buildModel();
	
	//Create Class
	Factory f = (Factory) spoon.getFactory();
	CtClass ctclass = f.createClass(srcClass+"_"+level);

	CtElement rootElement = spoon.getModel().getRootPackage()
			.getElements(new TypeFilter<CtElement>(CtElement.class)).get(0);
	
	System.out.println("rootElement:"+rootElement.toString());
	
	List<CtClass> klassList = rootElement.getElements(new TypeFilter<CtClass>(CtClass.class));
	for (CtClass ctKlass : klassList) {
		
		if(srcClass.equals(ctKlass.getQualifiedName())) {
			
			System.out.println("ctKlass.getPackage()"+ctKlass.getPackage()); 
//			ctKlass.getPackage();
			
			CtPackage p = f.Core().createPackage();
			p.setSimpleName(ctKlass.getPackage().getQualifiedName());
			ctclass.setParent(p);
			ctclass.setVisibility(ModifierKind.PUBLIC);
			
			
			Set<CtMethod> methods = ctKlass.getMethods();			
			for (CtMethod method : methods) {				
				if(M_ISO.equals(level)) {
					//Identify buggy source method
					if(method.getSimpleName().contains(buggySrcMethod)) {
						System.out.println("methods.toString()"+method.getSimpleName()); 
						System.out.println("methods.toString()"+method.toString());
					}else {
						
						ctclass.addMethod(method);
						
						
					}
				}
				
				
				
			}	
			
			
		}	
	}
	spoon.setSourceOutputDirectory(outputSrcDirectory);
	System.out.println("outputSrcDirectory"+outputSrcDirectory);
	JDTBasedSpoonCompiler jdtSpoonModelBuilder = new JDTBasedSpoonCompiler(f);
	jdtSpoonModelBuilder.setSourceOutputDirectory(outputSrcDirectory);
	jdtSpoonModelBuilder.generateProcessedSourceFiles(OutputType.COMPILATION_UNITS);
	jdtSpoonModelBuilder.compile(InputType.CTTYPES);
	jdtSpoonModelBuilder.generateProcessedSourceFiles(OutputType.CLASSES);
}






}