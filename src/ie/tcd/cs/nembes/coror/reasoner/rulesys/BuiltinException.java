/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.tcd.cs.nembes.coror.reasoner.rulesys;

import ie.tcd.cs.nembes.coror.shared.JenaException;

/**
 * Exceptions thrown by runtime errors in exceuting rule system
 * builtin operations.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.9 $ on $Date: 2008/01/02 12:07:46 $
 */
public class BuiltinException extends JenaException {

    /**
     * Constructor.
     * @param builtin the invoking builtin
     * @param context the invoking rule context
     * @param message a text explanation of the error
     */
    public BuiltinException(Builtin builtin, RuleContext context, String message) {
        super("Error in clause of rule (" + context.getRule().toShortString() + ") "
                                         + builtin.getName() + ": " + message);
    }
}
