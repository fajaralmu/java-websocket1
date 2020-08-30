<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<!-- CONTROLS -->
<table id="controls-pad" class="table-fixed" valign="top">
	<tr valign="top">
		<td><div>
				<button class="btn btn-secondary btn-sm" onclick="closeControlPad()">Close
					Control Pad</button>
			</div></td>
		<td><button class="control-btn" move-role="w" id="btn-up">UP
				-w</button></td>
		<td></td>
	</tr>
	<tr valign="top">
		<td><button class="control-btn" move-role="a" id="btn-left">LEFT
				-a</button></td>
		<td><button class="control-btn" style="background-color: red"
				onclick="releaseAll()" id="btn-stop">STOP</button></td>
		<td><button class="control-btn" move-role="d" id="btn-right">RIGHT
				-d</button></td>
	</tr>
	<tr valign="top">
		<td style="text-align: center"><p id="player-name"></p></td>
		<td><button class="control-btn" move-role="s" id="btn-down">DOWN
				-s</button></td>
		<td style="text-align: center"><p id="player-position"></p></td>
	</tr>
</table>
<div style="display: none" id="show-control-btn">
	<button class="btn btn-primary btn-sm" onclick="showControlPad()">Show
		Control Pad</button>
</div>

