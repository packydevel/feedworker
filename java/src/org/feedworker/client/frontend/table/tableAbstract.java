package org.feedworker.client.frontend.table;

import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.feedworker.client.frontend.events.TableEvent;
import org.feedworker.client.frontend.events.TableEventListener;

/**
 *
 * @author luca
 */
abstract class tableAbstract extends JTable implements TableEventListener {

    protected final Font font = new Font("Arial", Font.PLAIN, 11);

    public tableAbstract(String name) {
        super();
        setName(name);
        setFont(font);
        setFocusable(false);
        setRowSelectionAllowed(false);
        getTableHeader().setReorderingAllowed(false);
    }

    @Override
    abstract public void objReceived(TableEvent evt);

    abstract protected void lockColumns();

    public void removeAllRows() {
        ((DefaultTableModel) getModel()).setRowCount(0);
    }
}