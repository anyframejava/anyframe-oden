package org.eclipse.nebula.widgets.calendarcombo;

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class Tester {

	public static void main(String[] args) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setText("Calendar Combo Tester");
		shell.setSize(200, 400);
		
		// allow other date formats than default
		class Settings extends DefaultSettings {
			
			public Locale getLocale() {
/*				Locale [] locs = Locale.getAvailableLocales();
				for (int i = 0; i < locs.length; i++) {
					System.err.println(locs[i].getLanguage() + " " + locs[i].getDisplayCountry());	
				}
*/				
				return new Locale("ro");
			}

			public boolean keyboardNavigatesCalendar() {
				return false;
			}

			
		}
		
		shell.setLayout(new FillLayout());
		Composite inner = new Composite(shell, SWT.None);
		GridLayout gl = new GridLayout(1, true);		
		inner.setLayout(gl);

		Label foo = new Label(inner, SWT.NONE);
		foo.setText("Test");
		final CalendarCombo cc = new CalendarCombo(inner, SWT.NONE, new Settings(), null);

		final CalendarCombo cc2 = new CalendarCombo(inner, SWT.NONE, new Settings(), null);

		Button b = new Button(inner, SWT.PUSH);
		b.setText("Check date");
		b.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				System.err.println(cc.getDate().getTime());
			}
			
		});
	
		shell.open();

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
}
