package com.denspark.strelets.cinematrix.not_for_production;

import android.text.TextUtils;
import com.github.code.Itertools;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FilmixDecoder {
    Base64 base64 =new Base64();


    public static void main(String[] args) {
        FilmixDecoder decoder = new FilmixDecoder();
        String s_1 = "NW4dzGVfQcEXOCRnamRnyGAJO7AfzM6nB7zRy7akgiseac4jBjwUOGLdaHBdgjohOGlUa7AMam1VgF6cyC1wND1MQouhgbXhzi1=Q7sUy7zpLl4SN5ApLogGQooQwk12QCUIQGLRyCn0Qi9UaArr";

        String s_2 = "#2W3sidGl0bGUiOiIg0KHQtdC30L7QvSAxIiwiZm9sZGVyIjpbeyJ0aXRsZSI6ItCh0LXRgNC40Y8gMSAo0KHQtdC30L7QvSAxKSIsImlkIjoiczFlMSIsImZpbGUiOiJbMTA4MCBIRF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAxZTAxXzEwODAubXA0LFs0ODBwXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU//a2lub2NvdmVyLnc5OC5uamJo4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDFlMDFfNDgwLm1wNCxbNzIwcF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAxZTAxXzcyMC5tcDQifSx7InRpdGxlIjoi0KHQtdGA0LjRjyAyICjQodC10LfQvtC9IDEpIiwiaWQiOiJzMWUyIiwiZmlsZSI6IlsxMDgwIEhEXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDFlMDJfMTA4MC5tcDQsWzQ4MHBdaHR0cHM6Ly92czEuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMWUwMl80ODAubXA0LFs3MjBwXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDFlMDJfNzIwLm1wNCJ9LHsidGl0bGUiOiLQodC10YDQuNGPIDMgKNCh0LXQt9C+0L0gMSkiLCJpZCI6InMxZTMiLCJmaWxlIjoiWzEwODAgSERdaHR0cHM6Ly92czEuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMWUwM18xMDgwLm1wNCxbNDgwcF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAxZTAzXzQ4MC5tcDQsWzcyMHBdaHR0cHM6Ly92czEuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMWUwM183MjAubXA0In0seyJ0aXRsZSI6ItCh0LXRgNC40Y8gNCAo0KHQtdC30L7QvSAxKSIsImlkIjoiczFlNCIsImZpbGUiOiJbMTA4MCBIRF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAxZTA0XzEwODAubXA0LFs0ODBwXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDFlMDRfNDgwLm1wNCxbNzIwcF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAxZTA0XzcyMC5tcDQifSx7InRpdGxlIjoi0KHQtdGA0LjRjyA1ICjQodC10LfQvtC9IDEpIiwiaWQiOiJzMWU1IiwiZmlsZSI6IlsxMDgwIEhEXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDFlMDVfMTA4MC5tcDQsWzQ4MHBdaHR0cHM6Ly92czEuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMWUwNV80ODAubXA0LFs3MjBwXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDFlMDVfNzIwLm1wNCJ9LHsidGl0bGUiOiLQodC10YDQuNGPIDYgKNCh0LXQt9C+0L0gMSkiLCJpZCI6InMxZTYiLCJmaWxlIjoiWzEwODAgSERdaHR0cHM6Ly92czAuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMWUwNl8xMDgwLm1wNCxbNDgwcF1odHRwczovL3ZzMC5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAxZTA2XzQ4MC5tcDQsWzcyMHBdaHR0cHM6Ly92czAuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMWUwNl83MjAubXA0In0seyJ0aXRsZSI6ItCh0LXRgNC40Y8gNyAo0KHQtdC30L7QvSAxKSIsImlkIjoiczFlNyIsImZpbGUiOiJbMTA4MCBIRF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAxZTA3XzEwODAubXA0LFs0ODBwXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDFlMDdfNDgwLm1wNCxbNzIwcF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAxZTA3XzcyMC5tcDQifSx7InRpdGxlIjoi0KHQtdGA0LjRjyA4ICjQodC10LfQvtC9IDEpIiwiaWQiOiJzMWU4IiwiZmlsZSI6IlsxMDgwIEhEXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDFlMDhfMTA4MC5tcDQsWzQ4MHBdaHR0cHM6Ly92czEuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMWUwOF80ODAubXA0LFs3MjBwXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDFlMDhfNzIwLm1wNCJ9XX0seyJ0aXRsZSI6IiDQodC10LfQvtC9IDIiLCJmb2xkZXIiOlt7InRpdGxlIjoi0KHQtdGA0LjRjyAxICjQodC10LfQvtC9IDIpIiwiaWQiOiJzMmUxIiwiZmlsZSI6IlsxMDgwIEhEXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDJlMDFfMTA4MC5tcDQsWzQ4MHBdaHR0cHM6Ly92czEuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMmUwMV80ODAubXA0LFs3MjBwXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDJlMDFfNzIwLm1wNCJ9LHsidGl0bGUiOiLQodC10YDQuNGPIDIgKNCh0LXQt9C+0L0gMikiLCJpZCI6InMyZTIiLCJmaWxlIjoiWzEwODAgSERdaHR0cHM6Ly92czEuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMmUwMl8xMDgwLm1wNCxbNDgwcF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAyZTAyXzQ4MC5tcDQsWzcyMHBdaHR0cHM6Ly92czEuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMmUwMl83MjAubXA0In0seyJ0aXRsZSI6ItCh0LXRgNC40Y8gMyAo0KHQtdC30L7QvSAyKSIsImlkIjoiczJlMyIsImZpbGUiOiJbMTA4MCBIRF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAyZTAzXzEwODAubXA0LFs0ODBwXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDJlMDNfNDgwLm1wNCxbNzIwcF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAyZTAzXzcyMC5tcDQifSx7InRpdGxlIjoi0KHQtdGA0LjRjyA0ICjQodC10LfQvtC9IDIpIiwiaWQiOiJzMmU0IiwiZmlsZSI6IlsxMDgwIEhEXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDJlMDRfMTA4MC5tcDQsWzQ4MHBdaHR0cHM6Ly92czEuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMmUwNF80ODAubXA0LFs3MjBwXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDJlMDRfNzIwLm1wNCJ9LHsidGl0bGUiOiLQodC10YDQuNGPIDUgKNCh0LXQt9C+0L0gMikiLCJpZCI6InMyZTUiLCJmaWxlIjoiWzEwODAgSERdaHR0cHM6Ly92czEuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMmUwNV8xMDgwLm1wNCxbNDgwcF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAyZTA1XzQ4MC5tcDQsWzc//Y2VyY2EudHJvdmEuc2FnZ2V6emE=yMHBdaHR0cHM6Ly92czEuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMmUwNV83MjAubXA0In0seyJ0aXRsZSI6ItCh0LXRgNC40Y8gNiAo0KHQtdC30L7QvSAyKSIsImlkIjoiczJlNiIsImZpbGUiOiJbMTA4MCBIRF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAyZTA2XzEwODAubXA0LFs0ODBwXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDJlMDZfNDgwLm1wNCxbNzIwcF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAyZTA2XzcyMC5tcDQifSx7InRpdGxlIjoi0KHQtdGA0LjRjyA3ICjQodC10LfQvtC9IDIpIiwiaWQiOiJzMmU3IiwiZmlsZSI6IlsxMDgwIEhEXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDJlMDdfMTA4MC5tcDQsWzQ4MHBdaHR0cHM6Ly92czEuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMmUwN180ODAubXA0LFs3MjBwXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1Z//c2ljYXJpby4yMi5tb3ZpZXM=GMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDJlMDdfNzIwLm1wNCJ9LHsidGl0bGUiOiLQodC10YDQuNGPIDggKNCh0LXQt9C+0L0gMikiLCJpZCI6InMyZTgiLCJmaWxlIjoiWzEwODAgSERdaHR0cHM6Ly92czEuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMmUwOF8xMDgwLm1wNCxbNDgwcF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAyZTA4XzQ4MC5tcDQsWzcyMHBdaHR0cHM6Ly92czEuY2RubGFzdC5jb20vcy8yNWY4ZWRmMjVkYzJlOGFhYWQ0OWM0NTgzZWU5NmZlNzUyMDU5ZS9IYXBweS0yMDE3LWxvc3RmaWxtL3MwMmUwOF83MjAubXA0In0seyJ0aXRsZSI6ItCh0LXRgNC40Y8gOSAo0KHQtdC30L7QvSAyKSIsImlkIjoiczJlOSIsImZpbGUiOiJbMTA4MCBIRF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAyZTA5XzEwODAubXA0LFs0ODBwXWh0dHBzOi8vdnMxLmNkbmxhc3QuY29tL3MvMjVmOGVkZjI1ZGMyZThhYWFkNDljNDU4M2VlOTZmZTc1MjA1OWUvSGFwcHktMjAxNy1sb3N0ZmlsbS9zMDJlMDlfNDgwLm1wNCxbNzIwcF1odHRwczovL3ZzMS5jZG5sYXN0LmNvbS9zLzI1ZjhlZGYyNWRjMmU4YWFhZDQ5YzQ1ODNlZTk2ZmU3NTIwNTllL0hhcHB5LTIwMTctbG9zdGZpbG0vczAyZTA5XzcyMC5tcDQifV19XQ==";
        String s_3 = "#06807407407003a02f02f07303702e07407606206507307402e06e06507402f06606906c06d02f06602f03203003103702f06606f07207306506706505f03802e06d070034";
//
//        System.out.println(urlDecodeBase64(s_1));
//        System.out.println(urlDecodeBase64_v2(s_2));
//        System.out.println(urlDecodeUnicode(s_3));


        System.out.println(decoder.decodeMediaUrl(s_3));

    }

    private String urlDecodeBase64(String s) {
        String codec_a = "y5U4ei6d7NJgtG2VlBxfsQ1Hz=";
        String codec_b = "MXwR3m80TauZpDbokYnvIL9Wcr";
        String[] codec_a_arr = codec_a.split("");
        String[] codec_b_arr = codec_b.split("");
        for (int i = 0; i < codec_a_arr.length; i++) {
            s = s.replace(codec_a_arr[i], "*");
            s = s.replace(codec_b_arr[i], codec_a_arr[i]);
            s = s.replace("*", codec_b_arr[i]);
        }
        String decodedUrl = StringUtils.newStringUtf8(Base64.decodeBase64(s));
        return decodedUrl;
    }

    //    def decode_base64_2(self, encoded_url):
//    tokens = ("//Y2VyY2EudHJvdmEuc2FnZ2V6emE=", "//c2ljYXJpby4yMi5tb3ZpZXM=", "//a2lub2NvdmVyLnc5OC5uamJo")
//    clean_encoded_url = encoded_url[2:].replace("\/","/")
//
//        for token in tokens:
//    clean_encoded_url = clean_encoded_url.replace(token, "")
//
//            return base64.b64decode(clean_encoded_url)
    public String urlDecodeBase64_v2(String s) {
        String[] tokens = {
                "//Y2VyY2EudHJvdmEuc2FnZ2V6emE=",
                "//c2ljYXJpby4yMi5tb3ZpZXM=",
                "//a2lub2NvdmVyLnc5OC5uamJo"
        };
        String clean_encoded_url = s.substring(2).replace("\\/", "/");
        for (String token : tokens) {
            clean_encoded_url = clean_encoded_url.replace(token, "");
        }

        String decodedUrl = StringUtils.newStringUtf8(base64.decode(clean_encoded_url.getBytes()));
        return decodedUrl;
    }

    private String urlDecodeUnicode(String s) {
        String encodedUrl;
        if (s.contains("#")) {
            encodedUrl = s.substring(1);
        } else {
            encodedUrl = s;
        }
        StringBuilder unicodeUrl = new StringBuilder();
        for (List<String> stringList : specGrouper3L(encodedUrl)) {
            List<String> tempStringList = new ArrayList<>();
            tempStringList.add("\\u0");
            tempStringList.addAll(stringList);
            unicodeUrl.append(TextUtils.join("", tempStringList));
        }
        String nonUnicodeDecodedUrl = unicodeUrl.toString();

        return decodeUnicode(nonUnicodeDecodedUrl);
    }

    private static String decodeUnicode(String encoded_s) {
        encoded_s = StringEscapeUtils.unescapeJava(encoded_s);
        return encoded_s;
    }

    List<String> makeLinksList(String s) {
        List<String> linksList = new ArrayList<>();
        List<String> listOfEndings = listOfEndings(s);
        for (String quality : listOfEndings) {
            try {
                String resultString = s.replaceAll("\\[.*\\]", quality);
                linksList.add(resultString);
            } catch (PatternSyntaxException ex) {
                // Syntax error in the regular expression
            } catch (IllegalArgumentException ex) {
                // Syntax error in the replacement text (unescaped $ signs?)
            } catch (IndexOutOfBoundsException ex) {
                // Non-existent backreference used the replacement text
            }
        }
        return linksList;
    }

    List<String> listOfEndings(String s) {
        String ResultString = null;
        try {
            Pattern regex = Pattern.compile("\\[.*\\]");
            Matcher regexMatcher = regex.matcher(s);
            if (regexMatcher.find()) {
                ResultString = regexMatcher.group();
            }
        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
        }
        List<String> matchList = new ArrayList<String>();
        try {
            Pattern regex = Pattern.compile("[\\d]+[\\w]*");
            Matcher regexMatcher = regex.matcher(ResultString);
            while (regexMatcher.find()) {
                matchList.add(regexMatcher.group());
            }
        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
        }
        return matchList;
    }

    private Iterable<List<String>> specGrouper3L(String s) {
        ArrayList<String> strings = new ArrayList<>();
        for (char c : s.toCharArray()) {
            strings.add(Character.toString(c));
        }
        Iterator<String> stringIterator = strings.iterator();
        List<String> listOne = new ArrayList<>();
        List<String> listTwo = new ArrayList<>();
        List<String> listThree = new ArrayList<>();
        while (stringIterator.hasNext()) {

            listOne.add(stringIterator.next());

            if (stringIterator.hasNext()) {
                listTwo.add(stringIterator.next());
            }

            if (stringIterator.hasNext()) {
                listThree.add(stringIterator.next());
            }
        }
        return Itertools.izipLongest("x", listOne, listTwo, listThree);
    }

//    def decode_direct_media_url(self, encoded_url, checkhttp=False):
//            if(checkhttp == True and (encoded_url.find('http://') != -1 or encoded_url.find('https://') != -1)):
//            return False
//
//        try:
//                if encoded_url.find('#') != -1:
//                if encoded_url[:2] == '#2':
//            return self.decode_base64_2(encoded_url)
//            else:
//            return self.decode_unicode(encoded_url)
//            else:
//            return self.decode_base64(encoded_url)
//    except:
//            return False

    public String decodeMediaUrl(String encodedUrl) {
        if (encodedUrl.contains("#")) {
            if (encodedUrl.startsWith("#2")) {
                return urlDecodeBase64_v2(encodedUrl);
            } else {
                return urlDecodeUnicode(encodedUrl);
            }
        } else {
            return urlDecodeBase64(encodedUrl);
        }
    }

}
