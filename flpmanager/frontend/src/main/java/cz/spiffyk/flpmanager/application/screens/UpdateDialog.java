package cz.spiffyk.flpmanager.application.screens;

import java.io.IOException;

import cz.spiffyk.flpmanager.UpdateChecker.UpdateInfo;
import cz.spiffyk.flpmanager.util.FXUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.NonNull;

public class UpdateDialog extends Dialog<Boolean> {
	
	private final UpdateInfo info;

	@FXML private Label name;
	@FXML private WebView webView;
	
	public UpdateDialog(@NonNull UpdateInfo info) {
		super();
		this.info = info;
		this.setTitle("A new version is available!");
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/dialogs/UpdateDialog.fxml"));
		loader.setController(this);
		
		try {
			DialogPane pane = new DialogPane();
			
			pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			pane.setContent(loader.load());
			
			final Button btOk = (Button) pane.lookupButton(ButtonType.OK);
			btOk.addEventFilter(ActionEvent.ACTION, this::onOk);
			
			this.setDialogPane(pane);
		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit();
		}
	}
	
	@FXML private void initialize() {
		name.setText(info.getName());
		final WebEngine engine = webView.getEngine();
		engine.setUserStyleSheetLocation(getClass().getClassLoader().getResource("css/updatenotes.css").toExternalForm());
		engine.loadContent(info.getNotes(), "text/html");
	}
	
	private void onOk(ActionEvent event) {
		FXUtils.openWebPage(info.getUrl());
	}
}
