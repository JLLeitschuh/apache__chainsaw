/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.chainsaw;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.chainsaw.filter.FilterModel;
import org.apache.log4j.chainsaw.help.HelpManager;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.chainsaw.osx.OSXIntegration;
import org.apache.log4j.chainsaw.receivers.ReceiversHelper;


/**
 * Encapsulates the full Toolbar, and menus and all the actions that can be performed from it.
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 */
class ChainsawToolBarAndMenus implements ChangeListener {
  private final SmallToggleButton showReceiversButton;
  private final JTextField findField;
  private final Action changeModelAction;
  private final Action clearAction;
  private final Action closeAction;
  private final Action findNextAction;
  private final Action findPreviousAction;
  private final Action findPreviousColorizedEventAction;
  private final Action findNextColorizedEventAction;
  private final Action findNextMarkerAction;
  private final Action findPreviousMarkerAction;
  private final Action toggleMarkerAction;
  private final Action clearAllMarkersAction;
  private final Action pauseAction;
  private final Action showPreferencesAction;
  private final Action showColorPanelAction;
  private final Action showReceiversAction;
  private final Action toggleLogTreeAction;
  private final Action toggleScrollToBottomAction;
  private final Action scrollToTopAction;
  private final Action toggleDetailPaneAction;
  private final Action toggleToolbarAction;
  private final Action undockAction;
  private final Action customExpressionPanelAction;
  private final Collection lookAndFeelMenus = new ArrayList();
  private final JCheckBoxMenuItem toggleShowReceiversCheck =
    new JCheckBoxMenuItem();
  private final JCheckBoxMenuItem toggleLogTreeMenuItem =
    new JCheckBoxMenuItem();
  private final JCheckBoxMenuItem toggleScrollToBottomMenuItem =
    new JCheckBoxMenuItem();
  private final JCheckBoxMenuItem toggleDetailMenuItem =
    new JCheckBoxMenuItem();
  private final JCheckBoxMenuItem toggleCyclicMenuItem =
    new JCheckBoxMenuItem();
  private final FileMenu fileMenu;
  private final JCheckBoxMenuItem toggleStatusBarCheck =
    new JCheckBoxMenuItem();
  private final JMenu viewMenu = new JMenu("View");
  private final JMenuBar menuBar;
  private final JCheckBoxMenuItem menuItemClose = new JCheckBoxMenuItem();
  private final JToolBar toolbar;
  private final LogUI logui;
  private final SmallButton clearButton = new SmallButton();
  private final SmallToggleButton detailPaneButton = new SmallToggleButton();
  private final SmallToggleButton logTreePaneButton = new SmallToggleButton();
  private final SmallToggleButton scrollToBottomButton = new SmallToggleButton();
  private final SmallToggleButton pauseButton = new SmallToggleButton();
  private final SmallToggleButton toggleCyclicButton = new SmallToggleButton();
  private final Action[] logPanelSpecificActions;
  private final JMenu activeTabMenu = new JMenu("Current tab");
  private final JPanel findPanel;

    ChainsawToolBarAndMenus(final LogUI logui) {
    this.logui = logui;
    toolbar = new JToolBar(SwingConstants.HORIZONTAL);
    menuBar = new JMenuBar();
    fileMenu = new FileMenu(logui);
    closeAction = createCloseHelpAction();
    changeModelAction = createChangeModelAction();
    findField = new JTextField();
    findNextAction = getFindNextAction();
    findPreviousAction = getFindPreviousAction();
    findNextMarkerAction = createFindNextMarkerAction();
    findPreviousColorizedEventAction = getFindPreviousColorizedEventAction();
    findNextColorizedEventAction = getFindNextColorizedEventAction();
    findPreviousMarkerAction = createFindPreviousMarkerAction();
    toggleMarkerAction = createToggleMarkerAction();
    clearAllMarkersAction = createClearAllMarkersAction();
    customExpressionPanelAction = createCustomExpressionPanelAction();
    showPreferencesAction = createShowPreferencesAction();
    showColorPanelAction = createShowColorPanelAction();
    toggleToolbarAction = createToggleToolbarAction();
    toggleLogTreeAction = createToggleLogTreeAction();
    toggleScrollToBottomAction = createScrollToBottomAction();
    scrollToTopAction = createScrollToTopAction();
    pauseAction = createPauseAction();
    clearAction = createClearAction();
    undockAction = createUndockAction();
    showReceiversAction = createShowReceiversAction();
    showReceiversButton = new SmallToggleButton(showReceiversAction);

    findPanel = new JPanel();
    findPanel.setLayout(new BoxLayout(findPanel, BoxLayout.X_AXIS));

    Dimension findSize = new Dimension(260, 22);
    Dimension findPanelSize = new Dimension(310, 30);
    findPanel.setPreferredSize(findPanelSize);
    findPanel.setMaximumSize(findPanelSize);
    findPanel.setMinimumSize(findPanelSize);

    findField.setPreferredSize(findSize);
    findField.setMaximumSize(findSize);
    findField.setMinimumSize(findSize);
    findPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
    findField.setAlignmentY(Component.CENTER_ALIGNMENT);

    toggleDetailPaneAction = createToggleDetailPaneAction();
    createMenuBar();
    createToolbar();

    logPanelSpecificActions =
      new Action[] {
        pauseAction, findNextAction, findPreviousAction, findNextColorizedEventAction, findPreviousColorizedEventAction,
        findNextMarkerAction, findPreviousMarkerAction,
        toggleMarkerAction, clearAllMarkersAction, scrollToTopAction, clearAction,
        fileMenu.getFileSaveAction(), toggleDetailPaneAction,
        showPreferencesAction, showColorPanelAction, undockAction,
        toggleLogTreeAction, toggleScrollToBottomAction, changeModelAction,
      };

    logui.getApplicationPreferenceModel().addPropertyChangeListener(
      "statusBar",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          boolean value = ((Boolean) evt.getNewValue()).booleanValue();
          toggleStatusBarCheck.setSelected(value);
        }
      });

    logui.getApplicationPreferenceModel().addPropertyChangeListener(
      "receivers",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          boolean value = ((Boolean) evt.getNewValue()).booleanValue();
          showReceiversButton.setSelected(value);
          toggleShowReceiversCheck.setSelected(value);
        }
      });
  }

  /**
   * @return
   */
  private Action createChangeModelAction() {
    Action action =
      new AbstractAction("Use Cyclic", new ImageIcon(ChainsawIcons.REFRESH)) {
        public void actionPerformed(ActionEvent arg0) {
          LogPanel logPanel = logui.getCurrentLogPanel();
          logPanel.toggleCyclic();
          scanState();
        }
      };

    action.putValue(
      Action.SHORT_DESCRIPTION, "Changes between Cyclic and Unlimited mode.");

    return action;
  }

  /**
  * @return
  */
  private Action createToggleLogTreeAction() {
    Action action =
      new AbstractAction("Toggle the Logger Tree Pane") {
        public void actionPerformed(ActionEvent e) {
          if (logui.getCurrentLogPanel() != null) {
            logui.getCurrentLogPanel().toggleLogTreeVisible();
          }
        }
      };

    action.putValue(Action.SHORT_DESCRIPTION, "Toggles the Logger Tree Pane");
    action.putValue("enabled", Boolean.TRUE);
    action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_T));
    action.putValue(
      Action.ACCELERATOR_KEY,
      KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_MASK));
    action.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.WINDOW_ICON));

    return action;
  }

  /**
   * @return
   */
   private Action createScrollToBottomAction() {
     Action action =
       new AbstractAction("Toggle Scroll to Bottom") {
         public void actionPerformed(ActionEvent e) {
           if (logui.getCurrentLogPanel() != null) {
             logui.getCurrentLogPanel().toggleScrollToBottom();
           }
         }
       };

     action.putValue(Action.SHORT_DESCRIPTION, "Toggles Scroll to Bottom");
     action.putValue("enabled", Boolean.TRUE);
     action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_B));
     action.putValue(
       Action.ACCELERATOR_KEY,
       KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK));
     action.putValue(
       Action.SMALL_ICON, new ImageIcon(ChainsawIcons.SCROLL_TO_BOTTOM));

     return action;
   }

    private Action createScrollToTopAction() {
      Action action =
        new AbstractAction("Scroll to top") {
          public void actionPerformed(ActionEvent e) {
            if (logui.getCurrentLogPanel() != null) {
              logui.getCurrentLogPanel().scrollToTop();;
            }
          }
        };

      action.putValue(Action.SHORT_DESCRIPTION, "Scroll to top");
      action.putValue("enabled", Boolean.TRUE);
      action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_T));
      action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A,  InputEvent.CTRL_MASK));

      return action;
    }

     private Action createFindNextMarkerAction() {
       Action action =
         new AbstractAction("Find next marker") {
           public void actionPerformed(ActionEvent e) {
             if (logui.getCurrentLogPanel() != null) {
               logui.getCurrentLogPanel().findNextMarker();
             }
           }
         };

       action.putValue(Action.SHORT_DESCRIPTION, "Searches for the next marker from the current location");
       action.putValue("enabled", Boolean.TRUE);
       action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
       action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F2"));

       return action;
     }

    private Action createFindPreviousMarkerAction() {
      Action action =
        new AbstractAction("Find previous marker") {
          public void actionPerformed(ActionEvent e) {
            if (logui.getCurrentLogPanel() != null) {
              logui.getCurrentLogPanel().findPreviousMarker();
            }
          }
        };

      action.putValue(Action.SHORT_DESCRIPTION, "Searches for the previous marker from the current location");
      action.putValue("enabled", Boolean.TRUE);
      action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
      action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F2,  InputEvent.SHIFT_MASK));

      return action;
    }

    private Action createToggleMarkerAction() {
      Action action =
        new AbstractAction("Toggle marker") {
          public void actionPerformed(ActionEvent e) {
            if (logui.getCurrentLogPanel() != null) {
              logui.getCurrentLogPanel().toggleMarker();
            }
          }
        };

      action.putValue(Action.SHORT_DESCRIPTION, "Toggle marker for selected row");
      action.putValue("enabled", Boolean.TRUE);
      action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_T));
      action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F2,  InputEvent.CTRL_MASK));

      return action;
    }

    private Action createClearAllMarkersAction() {
      Action action =
        new AbstractAction("Clear all markers") {
          public void actionPerformed(ActionEvent e) {
            if (logui.getCurrentLogPanel() != null) {
              logui.getCurrentLogPanel().clearAllMarkers();
            }
          }
        };

      action.putValue(Action.SHORT_DESCRIPTION, "Removes all markers");
      action.putValue("enabled", Boolean.TRUE);
      action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
      action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F2,  InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));

      return action;
    }

   /**
   * DOCUMENT ME!
   */
  public void stateChange() {
    scanState();
  }

  /**
   * DOCUMENT ME!
   *
   * @param e DOCUMENT ME!
   */
  public void stateChanged(ChangeEvent e) {
    scanState();
  }

  JMenuBar getMenubar() {
    return menuBar;
  }

  JToolBar getToolbar() {
    return toolbar;
  }

  private Action createClearAction() {
    final Action action =
      new AbstractAction("Clear") {
        public void actionPerformed(ActionEvent e) {
          LogPanel logPanel = logui.getCurrentLogPanel();

          if (logPanel == null) {
            return;
          }

          logPanel.clearEvents();
        }
      };

    action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
    action.putValue(
      Action.ACCELERATOR_KEY,
      KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.CTRL_MASK));
    action.putValue(
      Action.SHORT_DESCRIPTION, "Removes all the events from the current view");
    action.putValue(Action.SMALL_ICON, new ImageIcon(ChainsawIcons.DELETE));

    return action;
  }

  private Action createCloseHelpAction() {
    final Action action =
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          closeAction.putValue(Action.NAME, "Welcome tab");
          logui.removeWelcomePanel();

          if (menuItemClose.isSelected()) {
            logui.addWelcomePanel();
          } else {
          }
        }
      };

    action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F1"));

    //    action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
    action.putValue(Action.SHORT_DESCRIPTION, "Toggles the Welcome tab");
    action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
    action.putValue(Action.NAME, "Welcome tab");

    return action;
  }

  private void createMenuBar() {
    JMenuItem menuItemUseRightMouse =
      new JMenuItem(
        "Other options available via panel's right mouse button popup menu");
    menuItemUseRightMouse.setEnabled(false);

    viewMenu.setMnemonic('V');

    final JCheckBoxMenuItem showToolbarCheck =
      new JCheckBoxMenuItem(toggleToolbarAction);
    showToolbarCheck.setSelected(
      logui.getApplicationPreferenceModel().isToolbar());

    logui.getApplicationPreferenceModel().addPropertyChangeListener(
      "toolbar",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          boolean value = ((Boolean) evt.getNewValue()).booleanValue();
          showToolbarCheck.setSelected(value);
        }
      });

    menuItemClose.setAction(closeAction);

    JCheckBoxMenuItem pause = new JCheckBoxMenuItem(pauseAction);
    JMenuItem menuPrefs = new JMenuItem(showPreferencesAction);
    menuPrefs.setText(
      showPreferencesAction.getValue(Action.SHORT_DESCRIPTION).toString());

    JMenuItem menuCustomExpressionPanel =
      new JMenuItem(customExpressionPanelAction);
    menuCustomExpressionPanel.setText(
      customExpressionPanelAction.getValue(Action.SHORT_DESCRIPTION).toString());

    JMenuItem menuShowColor = new JMenuItem(showColorPanelAction);
    menuShowColor.setText(
      showColorPanelAction.getValue(Action.SHORT_DESCRIPTION).toString());

    JMenuItem menuUndock = new JMenuItem(undockAction);

    JMenuItem showAppPrefs =
      new JMenuItem("Show Application-wide Preferences...");

    showAppPrefs.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          logui.showApplicationPreferences();
        }
      });

    toggleDetailMenuItem.setAction(toggleDetailPaneAction);
    toggleDetailMenuItem.setSelected(true);

    toggleCyclicMenuItem.setAction(changeModelAction);

    toggleCyclicMenuItem.setSelected(true);

    toggleLogTreeMenuItem.setAction(toggleLogTreeAction);
    toggleLogTreeMenuItem.setSelected(true);

    toggleScrollToBottomMenuItem.setAction(toggleScrollToBottomAction);

    final Action toggleStatusBarAction =
      new AbstractAction("Show Status bar") {
        public void actionPerformed(ActionEvent arg0) {
          logui.getApplicationPreferenceModel().setStatusBar(
            toggleStatusBarCheck.isSelected());
        }
      };

    toggleStatusBarAction.putValue(
      Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_B));
    toggleStatusBarCheck.setAction(toggleStatusBarAction);
    toggleStatusBarCheck.setSelected(
      logui.getApplicationPreferenceModel().isStatusBar());

    activeTabMenu.add(pause);
    activeTabMenu.add(toggleCyclicMenuItem);
    activeTabMenu.addSeparator();
    activeTabMenu.add(toggleDetailMenuItem);
    activeTabMenu.add(toggleLogTreeMenuItem);
    activeTabMenu.addSeparator();
    activeTabMenu.add(menuUndock);
    activeTabMenu.add(menuShowColor);
    activeTabMenu.add(menuPrefs);

    activeTabMenu.addSeparator();
    activeTabMenu.add(new CopyEventsToClipboardAction(logui));
    activeTabMenu.add(new JMenuItem(clearAction));

    activeTabMenu.addSeparator();
    activeTabMenu.add(new JMenuItem(toggleMarkerAction));
    activeTabMenu.add(new JMenuItem(findNextMarkerAction));
    activeTabMenu.add(new JMenuItem(findPreviousMarkerAction));
    activeTabMenu.add(new JMenuItem(clearAllMarkersAction));

    activeTabMenu.add(new JMenuItem(findNextColorizedEventAction));
    activeTabMenu.add(new JMenuItem(findPreviousColorizedEventAction));

    activeTabMenu.addSeparator();
    activeTabMenu.add(new JMenuItem(scrollToTopAction));
    activeTabMenu.add(toggleScrollToBottomMenuItem);
    activeTabMenu.add(menuItemUseRightMouse);
    
    viewMenu.add(showToolbarCheck);
    viewMenu.add(toggleStatusBarCheck);
    viewMenu.add(toggleShowReceiversCheck);
    viewMenu.add(menuItemClose);
    viewMenu.addSeparator();
    viewMenu.add(menuCustomExpressionPanel);

    if (!OSXIntegration.IS_OSX) {
        viewMenu.addSeparator();
        viewMenu.add(showAppPrefs);
    }

    JMenu helpMenu = new JMenu("Help");
    helpMenu.setMnemonic('H');

    JMenuItem about = new JMenuItem("About Chainsaw v2...");
    about.setMnemonic('A');
    about.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          logui.showAboutBox();
        }
      });

    Action startTutorial =
      new AbstractAction("Tutorial...", new ImageIcon(ChainsawIcons.HELP)) {
        public void actionPerformed(ActionEvent e) {
          logui.setupTutorial();
        }
      };

    startTutorial.putValue(
      Action.SHORT_DESCRIPTION, "Starts the tutorial process");
    helpMenu.add(startTutorial);

    List knownReceivers =
      ReceiversHelper.getInstance().getKnownReceiverClasses();
    JMenu receiverHelp = new JMenu("Receiver JavaDoc");

    for (Iterator iter = knownReceivers.iterator(); iter.hasNext();) {
      final Class clazz = (Class) iter.next();
      receiverHelp.add(
        new AbstractAction(clazz.getName()) {
          public void actionPerformed(ActionEvent arg0) {
            HelpManager.getInstance().showHelpForClass(clazz);
          }
        });
    }

    helpMenu.add(receiverHelp);

    helpMenu.addSeparator();
    helpMenu.add(CommonActions.getInstance().getShowReleaseNotes());
    helpMenu.add(about);

    menuBar.add(fileMenu);
    menuBar.add(viewMenu);
    menuBar.add(activeTabMenu);
    menuBar.add(helpMenu);
  }

  private Action createPauseAction() {
    final Action action =
      new AbstractAction("Pause") {
        public void actionPerformed(ActionEvent evt) {
          LogPanel logPanel = logui.getCurrentLogPanel();

          if (logPanel == null) {
            return;
          }

          logPanel.setPaused(!logPanel.isPaused());
          scanState();
        }
      };

    action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
    action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F12"));
    action.putValue(
      Action.SHORT_DESCRIPTION,
      "Causes incoming events for this tab to be discarded");
    action.putValue(Action.SMALL_ICON, new ImageIcon(ChainsawIcons.PAUSE));

    return action;
  }

  private Action createShowPreferencesAction() {
    Action showPreferences =
      new AbstractAction("", ChainsawIcons.ICON_PREFERENCES) {
        public void actionPerformed(ActionEvent arg0) {
          LogPanel logPanel = logui.getCurrentLogPanel();

          if (logPanel != null) {
            logPanel.showPreferences();
          }
        }
      };

    showPreferences.putValue(
      Action.SHORT_DESCRIPTION, "LogPanel Preferences...");

    // TODO think of good mnemonics and HotKey for this action
    return showPreferences;
  }

  private Action createCustomExpressionPanelAction() {
    final JDialog dialog = new JDialog(logui, "Define tab", true);
    dialog.getContentPane().add(getCustomExpressionPanel());
    dialog.setLocationRelativeTo(null);
    dialog.pack();

    Action createExpressionPanel =
      new AbstractAction("", ChainsawIcons.ICON_HELP) {
        public void actionPerformed(ActionEvent arg0) {
          dialog.setVisible(true);
        }
      };

    createExpressionPanel.putValue(
      Action.SHORT_DESCRIPTION, "Create custom expression LogPanel...   ");

    // TODO think of good mnemonics and HotKey for this action
    return createExpressionPanel;
  }

  private Action createShowColorPanelAction() {
    Action showColorPanel =
      new AbstractAction("", ChainsawIcons.ICON_PREFERENCES) {
        public void actionPerformed(ActionEvent arg0) {
          LogPanel logPanel = logui.getCurrentLogPanel();

          if (logPanel != null) {
            logPanel.showColorPreferences();
          }
        }
      };

    showColorPanel.putValue(
      Action.SHORT_DESCRIPTION, "LogPanel Color Filter...");

    // TODO think of good mnemonics and HotKey for this action
    return showColorPanel;
  }

  /**
   * @return
   */
  private Action createShowReceiversAction() {
    final Action action =
      new AbstractAction("Show Receivers") {
        public void actionPerformed(ActionEvent arg0) {
          logui.getApplicationPreferenceModel().setReceivers(
            !logui.getApplicationPreferenceModel().isReceivers());
        }
      };

    action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
    action.putValue(
      Action.SHORT_DESCRIPTION,
      "Shows the currently configured Log4j Receivers");
    action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F6"));
    action.putValue(
      Action.SMALL_ICON, new ImageIcon(ChainsawIcons.ANIM_NET_CONNECT));
    toggleShowReceiversCheck.setAction(action);

    return action;
  }

  private Action createToggleDetailPaneAction() {
    Action action =
      new AbstractAction("Show Detail Pane") {
        public void actionPerformed(ActionEvent evt) {
          LogPanel logPanel = logui.getCurrentLogPanel();

          if (logPanel == null) {
            return;
          }

          logPanel.toggleDetailVisible();
        }
      };

    action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
    action.putValue(
      Action.ACCELERATOR_KEY,
      KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_MASK));
    action.putValue(Action.SHORT_DESCRIPTION, "Hides/Shows the Detail Pane");
    action.putValue(Action.SMALL_ICON, new ImageIcon(ChainsawIcons.INFO));

    return action;
  }

  private Action createToggleToolbarAction() {
    /**
     * -== Begin of Show/Hide toolbar action
     */
    final Action action =
      new AbstractAction("Show Toolbar") {
        public void actionPerformed(ActionEvent e) {
          logui.getApplicationPreferenceModel().setToolbar(
            !logui.getApplicationPreferenceModel().isToolbar());
        }
      };

    action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_T));

    return action;
  }

  private void createToolbar() {
    Insets buttonMargins = new Insets(1, 1, 1, 1);

    FileMenu menu = (FileMenu) menuBar.getMenu(0);

    JButton fileOpenButton = new SmallButton(menu.getLog4JFileOpenAction());
    fileOpenButton.setMargin(buttonMargins);

    JButton fileSaveButton = new SmallButton(menu.getFileSaveAction());
    fileSaveButton.setMargin(buttonMargins);

    fileOpenButton.setText("");
    fileSaveButton.setText("");

    toolbar.add(fileOpenButton);
    toolbar.add(fileSaveButton);
    toolbar.addSeparator();

    pauseButton.setAction(pauseAction);
    pauseButton.setText("");

    //		pauseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F12"),pauseAction.getValue(Action.NAME) );
    pauseButton.getActionMap().put(
      pauseAction.getValue(Action.NAME), pauseAction);

    toggleCyclicButton.setAction(changeModelAction);
    toggleCyclicButton.setText(null);

    detailPaneButton.setAction(toggleDetailPaneAction);
    detailPaneButton.setText(null);
    detailPaneButton.getActionMap().put(
      toggleDetailPaneAction.getValue(Action.NAME), toggleDetailPaneAction);
    detailPaneButton.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
      KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_MASK),
      toggleDetailPaneAction.getValue(Action.NAME));

    logTreePaneButton.setAction(toggleLogTreeAction);
    logTreePaneButton.getActionMap().put(
      toggleLogTreeAction.getValue(Action.NAME), toggleLogTreeAction);
    logTreePaneButton.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
      KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_MASK),
      toggleLogTreeAction.getValue(Action.NAME));
    logTreePaneButton.setText(null);

    scrollToBottomButton.setAction(toggleScrollToBottomAction);
    scrollToBottomButton.getActionMap().put(
      toggleScrollToBottomAction.getValue(Action.NAME), toggleScrollToBottomAction);
    scrollToBottomButton.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
      KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK),
      toggleScrollToBottomAction.getValue(Action.NAME));
    scrollToBottomButton.setText(null);

    SmallButton prefsButton = new SmallButton(showPreferencesAction);
    SmallButton undockButton = new SmallButton(undockAction);
    undockButton.setText("");

    toolbar.add(undockButton);
    toolbar.add(pauseButton);
    toolbar.add(toggleCyclicButton);
    toolbar.addSeparator();
    toolbar.add(detailPaneButton);
    toolbar.add(logTreePaneButton);
    toolbar.add(scrollToBottomButton);
    toolbar.add(prefsButton);
    toolbar.addSeparator();

    toolbar.add(clearButton);
    clearButton.setAction(clearAction);
    clearButton.setText("");
    toolbar.addSeparator();

    JButton findNextButton = new SmallButton(findNextAction);
    findNextButton.setText("");
    findNextButton.getActionMap().put(
      findNextAction.getValue(Action.NAME), findNextAction);
    findNextButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
      (KeyStroke) findNextAction.getValue(Action.ACCELERATOR_KEY),
      findNextAction.getValue(Action.NAME));

    JButton findPreviousButton = new SmallButton(findPreviousAction);
    findPreviousButton.setText("");
    findPreviousButton.getActionMap().put(
      findPreviousAction.getValue(Action.NAME), findPreviousAction);
    findPreviousButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
      (KeyStroke) findPreviousAction.getValue(Action.ACCELERATOR_KEY),
      findPreviousAction.getValue(Action.NAME));

    JLabel searchLabel = new JLabel("Search:");
    searchLabel.setDisplayedMnemonic('j');
    searchLabel.setFont(searchLabel.getFont().deriveFont(Font.BOLD));
    findPanel.add(searchLabel);
    findPanel.add(Box.createHorizontalStrut(3));
    findPanel.add(findField);
    findPanel.add(Box.createRigidArea(new Dimension(5, 0)));
    searchLabel.setLabelFor(findField);


    toolbar.add(findPanel);
    toolbar.addSeparator(new Dimension(7, 5));
    toolbar.add(findNextButton);
    toolbar.add(findPreviousButton);

    toolbar.addSeparator();

    showReceiversButton.setText(null);
    toolbar.add(showReceiversButton);

    toolbar.add(Box.createHorizontalGlue());

    toolbar.setMargin(buttonMargins);
    toolbar.setFloatable(false);
  }

  private Action createUndockAction() {
    Action action =
      new AbstractAction("Undock", ChainsawIcons.ICON_UNDOCK) {
        public void actionPerformed(ActionEvent arg0) {
          LogPanel logPanel = logui.getCurrentLogPanel();

          if (logPanel != null) {
            logPanel.undock();
          }
        }
      };

    action.putValue(
      Action.SHORT_DESCRIPTION,
      "Undocks the current Log panel into its own window");

    //	TODO think of some mnemonics and HotKeys for this action
    return action;
  }

  private void scanState() {
    toggleStatusBarCheck.setSelected(logui.isStatusBarVisible());
    toggleShowReceiversCheck.setSelected(
      logui.getApplicationPreferenceModel().isReceivers());

    logTreePaneButton.setSelected(logui.isLogTreePanelVisible());
    LogPanel panel = logui.getCurrentLogPanel();
    if (panel != null) {
    	scrollToBottomButton.setSelected(panel.isScrollToBottom());
    } else {
    	scrollToBottomButton.setSelected(false);
    }
    showReceiversButton.setSelected(
      logui.getApplicationPreferenceModel().isReceivers());
    menuItemClose.setSelected(logui.getTabbedPane().containsWelcomePanel());

    /**
     * We get the currently selected LogPanel, and if null, deactivate some
     * actions
     */
    LogPanel logPanel = logui.getCurrentLogPanel();

    boolean activateLogPanelActions = true;

    if (logPanel == null) {
      activateLogPanelActions = false;
      findField.setEnabled(false);
      findPanel.removeAll();
      JLabel searchLabel = new JLabel("Search:");
      searchLabel.setDisplayedMnemonic('j');
      searchLabel.setFont(searchLabel.getFont().deriveFont(Font.BOLD));
      findPanel.add(searchLabel);
      findPanel.add(Box.createHorizontalStrut(3));

      findPanel.add(findField);
      findPanel.add(Box.createRigidArea(new Dimension(5, 0)));
      searchLabel.setLabelFor(findField);
      activeTabMenu.setEnabled(false);
      closeAction.setEnabled(true);
      detailPaneButton.setSelected(false);
      toggleCyclicButton.setSelected(false);
    } else {
      activeTabMenu.setEnabled(true);
      fileMenu.getFileSaveAction().setEnabled(true);
      findPanel.removeAll();
      JLabel searchLabel = new JLabel("Search:");
      searchLabel.setDisplayedMnemonic('j');

      searchLabel.setFont(searchLabel.getFont().deriveFont(Font.BOLD));
      findPanel.add(searchLabel);
      findPanel.add(Box.createHorizontalStrut(3));
      findPanel.add(logPanel.getFindTextField());
      findPanel.add(Box.createRigidArea(new Dimension(5, 0)));
      searchLabel.setLabelFor(logPanel.getFindTextField());

      pauseButton.getModel().setSelected(logPanel.isPaused());
      toggleCyclicButton.setSelected(logPanel.isCyclic());
      logui.getStatusBar().setPaused(logPanel.isPaused(), logPanel.getIdentifier());
      toggleCyclicMenuItem.setSelected(logPanel.isCyclic());
      detailPaneButton.getModel().setSelected(logPanel.isDetailVisible());
      toggleLogTreeMenuItem.setSelected(logPanel.isLogTreeVisible());
      toggleScrollToBottomMenuItem.setSelected(logPanel.isScrollToBottom());
    }

    findPanel.invalidate();
    findPanel.revalidate();
    findPanel.repaint();

    for (int i = 0; i < logPanelSpecificActions.length; i++) {
      logPanelSpecificActions[i].setEnabled(activateLogPanelActions);
    }

    String currentLookAndFeelName = UIManager.getLookAndFeel().getName();

    for (Iterator iter = lookAndFeelMenus.iterator(); iter.hasNext();) {
      JRadioButtonMenuItem element = (JRadioButtonMenuItem) iter.next();

      if (element.getText().equals(currentLookAndFeelName)) {
        element.setSelected(true);
      } else {
        element.setSelected(false);
      }
    }
  }

  private Action getFindNextAction() {
    final Action action =
      new AbstractAction("Find next") {
        public void actionPerformed(ActionEvent e) {
          LogPanel p = logui.getCurrentLogPanel();

          if (p != null) {
            p.findNext();
          }
        }
      };

    //    action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
    action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F3"));
    action.putValue(
      Action.SHORT_DESCRIPTION,
      "Find the next occurrence of the rule from the current row");
    action.putValue(Action.SMALL_ICON, new ImageIcon(ChainsawIcons.DOWN));

    return action;
  }

  private Action getFindPreviousAction() {
    final Action action =
      new AbstractAction("Find previous") {
        public void actionPerformed(ActionEvent e) {
          LogPanel p = logui.getCurrentLogPanel();

          if (p != null) {
            p.findPrevious();
          }
        }
      };

    //    action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
    action.putValue(
      Action.ACCELERATOR_KEY,
      KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.SHIFT_MASK));
    action.putValue(
      Action.SHORT_DESCRIPTION,
      "Find the previous occurrence of the rule from the current row");
    action.putValue(Action.SMALL_ICON, new ImageIcon(ChainsawIcons.UP));

    return action;
  }

    private Action getFindNextColorizedEventAction() {
      final Action action =
        new AbstractAction("Find next colorized event") {
          public void actionPerformed(ActionEvent e) {
            LogPanel p = logui.getCurrentLogPanel();
            if (p != null) {
              p.findNextColorizedEvent();
            }
          }
        };
        action.putValue(Action.SHORT_DESCRIPTION, "Searches for the next colorized event from the current location");
        action.putValue("enabled", Boolean.TRUE);
        action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));

      return action;
    }

    private Action getFindPreviousColorizedEventAction() {
      final Action action =
        new AbstractAction("Find previous colorized event") {
          public void actionPerformed(ActionEvent e) {
            LogPanel p = logui.getCurrentLogPanel();

            if (p != null) {
              p.findPreviousColorizedEvent();
            }
          }
        };
        action.putValue(Action.SHORT_DESCRIPTION, "Searches for the next colorized event from the current location");
        action.putValue("enabled", Boolean.TRUE);
        action.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));

      return action;
    }

  private JPanel getCustomExpressionPanel() {
    final JPanel panel = new JPanel(new BorderLayout());
    panel.add(
      new JLabel("Enter expression for new tab:  "), BorderLayout.NORTH);

    final JTextField textField = new JTextField();
    textField.addKeyListener(
      new ExpressionRuleContext(new FilterModel(), textField));
    panel.add(textField, BorderLayout.CENTER);

    JButton ok = new JButton("OK");
    JButton close = new JButton("Close");
    JPanel lowerPanel = new JPanel();
    lowerPanel.add(ok);
    lowerPanel.add(Box.createHorizontalStrut(7));
    lowerPanel.add(close);
    panel.add(lowerPanel, BorderLayout.SOUTH);

    ok.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          logui.createCustomExpressionLogPanel(textField.getText());
          SwingUtilities.getAncestorOfClass(JDialog.class, panel).setVisible(
            false);
        }
      });

    close.addActionListener(
      new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          SwingUtilities.getAncestorOfClass(JDialog.class, panel).setVisible(
            false);
        }
      });

    //String expression = JOptionPane.showInputDialog(logui, "Enter expression", "Create custom expression LogPanel", JOptionPane.PLAIN_MESSAGE);
    return panel;
  }
}
