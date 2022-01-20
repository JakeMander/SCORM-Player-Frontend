package com.example.myapplication.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.myapplication.data.LoginRepository;
import com.example.myapplication.data.Result;
import com.example.myapplication.data.model.LoggedInUser;
import com.example.myapplication.R;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        String hashedPass = hashPassword((password));
        Call<LoggedInUser> loginTask = loginRepository.login(username, hashedPass);

        loginTask.enqueue(new Callback<LoggedInUser>() {
            @Override
            public void onResponse(Call<LoggedInUser> call, Response<LoggedInUser> response) {
                if (response.isSuccessful()) {
                    handleSuccessfulResponse(response.body());
                }

                else {
                    handleUnsuccessfulResponse();
                }
            }

            @Override
            public void onFailure(Call<LoggedInUser> call, Throwable t) {
                handleUnsuccessfulResponse();
            }
        });
    }

    /**
     * Hashes a provided password string using the ... algorithm
     * @param passwordToHash The user password to run through the xxx Hash
     * @return A string representing the hashed output of the provided password
     * input.
     */
    private String hashPassword(String passwordToHash)
    {
        final SHA3.DigestSHA3 sha3512Hasher = new SHA3.Digest512();
        sha3512Hasher.update(passwordToHash.getBytes(StandardCharsets.UTF_8));
        byte[] hashedBytes = sha3512Hasher.digest();
        return EncodeHashedBytes(hashedBytes);
    }

    private String EncodeHashedBytes(byte[] hashedBytes)
    {
        StringBuffer sb = new StringBuffer();

        for (byte byt : hashedBytes)
        {
            sb.append(String.format("%02x", byt & 0xFF));
        }
        return sb.toString();
    }

    private void handleSuccessfulResponse(LoggedInUser loggedInUser) {
        LoggedInUserView userView = new LoggedInUserView(loggedInUser.getUsername());
        LoginResult result = new LoginResult(userView);
        loginResult.setValue(result);
    }

    private void handleUnsuccessfulResponse()
    {
        LoginResult result = new LoginResult(R.string.login_failed);
        loginResult.setValue(result);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}