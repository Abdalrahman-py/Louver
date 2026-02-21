package com.example.louver.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.louver.data.auth.AuthState;
import com.example.louver.R;
import com.example.louver.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {

    private AuthViewModel vm;

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private CircularProgressIndicator progress;
    private View tvGoRegister;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        progress = view.findViewById(R.id.progress);
        tvGoRegister = view.findViewById(R.id.tvGoRegister);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvGoRegister.setOnClickListener(v -> {
            ViewPager2 vp = requireActivity().findViewById(R.id.viewPager);
            if (vp != null) vp.setCurrentItem(1, true);
        });

        vm.loginState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            boolean loading = state.status == AuthState.Status.LOADING;
            setLoading(loading);

            if (state.status == AuthState.Status.ERROR) {
                Snackbar.make(view, state.message != null ? state.message : "Login failed.", Snackbar.LENGTH_LONG).show();
            } else if (state.status == AuthState.Status.SUCCESS) {
                startActivity(new Intent(requireContext(), MainActivity.class));
                requireActivity().finish();
            }
        });
    }

    private void attemptLogin() {
        clearErrors();

        String email = textOf(etEmail);
        String passStr = textOf(etPassword);

        boolean ok = true;

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email");
            ok = false;
        }

        if (passStr.length() < 8) {
            tilPassword.setError("Password must be at least 8 characters");
            ok = false;
        }

        if (!ok) return;

        char[] password = passStr.toCharArray();
        vm.login(email, password);
        // Note: password array is wiped by PasswordHasher.verifyPassword() on the background thread.
        etPassword.setText("");
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void clearErrors() {
        tilEmail.setError(null);
        tilPassword.setError(null);
    }

    private static String textOf(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
