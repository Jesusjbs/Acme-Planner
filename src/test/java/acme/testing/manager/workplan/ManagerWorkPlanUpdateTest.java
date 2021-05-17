/*
 * EmployerApplicationUpdateTest.java
 *
 * Copyright (C) 2012-2021 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.testing.manager.workplan;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import acme.testing.AcmePlannerTest;

public class ManagerWorkPlanUpdateTest extends AcmePlannerTest {

	// Lifecycle management ---------------------------------------------------
	
	// Test cases -------------------------------------------------------------
	//Este test positivo lo que comprueba es la correcta actualización de un workplan, para ello accedemos a uno dado, actualizamos los datos y
	//le damos al botón de actualizar, luego nos dirijimos al listado de workplan y nos metemos dentro del que habíamos actualizado
	// y finalmente comprobamos que los datos están actualizados correctamente
	@ParameterizedTest
	@CsvFileSource(resources = "/manager/workplan/upgrade-positive.csv", encoding = "utf-8", numLinesToSkip = 1)
	@Order(20)	
	public void updatePositive(final int recordIndex, final String title, final String beginning, final String ending, final String privacy) {		
		super.signIn("manager2", "manager2");
		
		super.clickOnMenu("Manager", "List workplans");		
		
		super.clickOnListingRecord(recordIndex);
		
		super.fillInputBoxIn("title", title);
		super.fillInputBoxIn("beginning", beginning);
		super.fillInputBoxIn("ending", ending);
		super.fillInputBoxIn("privacy", privacy);		
		
		super.clickOnSubmitButton("Update");
		
		super.checkColumnHasValue(recordIndex, 0, title);
		super.checkColumnHasValue(recordIndex, 1, beginning);
		super.checkColumnHasValue(recordIndex, 2, ending);
		//No compruebo el campo workload debido a que es un campo autogenerado y no cabe posibilidad a fallo
		super.checkColumnHasValue(recordIndex, 4, privacy);
		
		super.clickOnListingRecord(recordIndex);
		
		super.checkInputBoxHasValue("title", title);
		super.checkInputBoxHasValue("beginning", beginning);
		super.checkInputBoxHasValue("ending", ending);
		
		super.signOut();
	}
	 //Aquí comprobamos la actualización de los campos de workplan de forma fallida, y el fichero de error realmente es similar
	//al que usamos en el test de crear de forma negativa un workplan debido que los campos son los mismos. 
	@ParameterizedTest
	@CsvFileSource(resources = "/manager/workplan/upgrade-negative.csv", encoding = "utf-8", numLinesToSkip = 1)
	@Order(20)	
	public void updateNegative(final int recordIndex, final String title, final String beginning, final String ending, final String privacy) {		
		super.signIn("manager2", "manager2");
		
		super.clickOnMenu("Manager", "List workplans");		
		
		super.clickOnListingRecord(recordIndex);
		
		super.fillInputBoxIn("title", title);
		super.fillInputBoxIn("beginning", beginning);
		super.fillInputBoxIn("ending", ending);
		super.fillInputBoxIn("privacy", privacy);		
		
		
		super.signOut();
	}
	
	// Ancillary methods ------------------------------------------------------


}