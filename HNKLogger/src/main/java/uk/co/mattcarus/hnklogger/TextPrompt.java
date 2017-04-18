package uk.co.mattcarus.hnklogger;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class TextPrompt extends JLabel
    implements FocusListener, DocumentListener
{
    private JTextComponent component;
    private Document document;

    public TextPrompt(String text, JTextComponent component)
    {
    	System.out.println("Adding Prompt: " + text);
    	this.component = component;
    	document = component.getDocument();

    	setText( text );
    	setFont( component.getFont() );
    	setBorder( new EmptyBorder(component.getInsets()) );

    	component.addFocusListener( this );
    	document.addDocumentListener( this );

    	component.add( this );
    }

    public void checkForPrompt()
    {
    	if (document.getLength() == 0)
    		setSize( component.getSize() );
    	else
    		setSize(0, 0);
    }

//  Implement FocusListener

    public void focusGained(FocusEvent e)
    {
    	checkForPrompt();
    }

    public void focusLost(FocusEvent e)
    {
    	setSize(0, 0);
    }

//  Implement DocumentListener

    public void insertUpdate(DocumentEvent e)
    {
    	checkForPrompt();
    }

    public void removeUpdate(DocumentEvent e)
    {
    	checkForPrompt();
    }

    public void changedUpdate(DocumentEvent e) {}
}