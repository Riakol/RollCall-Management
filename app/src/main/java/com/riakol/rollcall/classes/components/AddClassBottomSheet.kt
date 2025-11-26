package com.riakol.rollcall.classes.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.riakol.rollcall.ui.theme.BackgroundDark
import com.riakol.rollcall.ui.theme.PrimaryBlue
import com.riakol.rollcall.ui.theme.SurfaceDark
import com.riakol.rollcall.ui.theme.TextGray
import com.riakol.rollcall.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClassBottomSheet(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Локальное состояние полей ввода
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Валидация
    val isFormValid = name.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundDark,
        dragHandle = {
            androidx.compose.material3.BottomSheetDefaults.DragHandle(color = TextGray)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = "Новый класс",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название (например, 9 'А')", color = TextGray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = TextGray,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание (предмет или заметка)", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = TextGray,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Отмена", color = TextGray, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { onSave(name, description) },
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        disabledContainerColor = SurfaceDark,
                        contentColor = Color.White,
                        disabledContentColor = TextGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Создать", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}